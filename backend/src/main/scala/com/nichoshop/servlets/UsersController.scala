package com.nichoshop.servlets

import com.nichoshop.legacy.models.UsersRow
import com.nichoshop.marshalling.Marshallers
import com.nichoshop.models.helpers.DB
import com.nichoshop.models.{PasswordResetEntity, PasswordResets, Users}
import com.nichoshop.services.UserService
import com.nichoshop.services.util.{Emailer, Twilio}
import com.nichoshop.servlets.UsersController._
import com.nichoshop.servlets.swagger.UsersOperations
import com.nichoshop.utils.auth.SecureUtils
import org.scalatra.swagger._
import org.scalatra.{Accepted, BadRequest, NotFound}
import org.slf4j.LoggerFactory

import java.sql.Timestamp
import java.util.concurrent.TimeUnit
import scala.util.Random

class UsersController(userService: UserService)
                     (implicit val swagger: Swagger)
  extends AuthServlet with Json with UsersOperations with GenController[UsersRow] {  //TODO remove gen controller
  private val log = LoggerFactory.getLogger(getClass)
  private val r = new Random()
  //      before() {
  //        requireLogin()
  //    }

  def service = userService

/*
  get("/logged_in", operation(loggedIn)) {
    okOrNotFound[UsersRow](authService.fromSession(session.getId))
  }
*/

  post("/restore", operation(restore)) {
    try {
      val p = parsedBody.extract[RestorePassword]
      var found = false
      DB.write { implicit session =>
        p.userid.flatMap(Users.findByEmailOrUserid).foreach { user =>
          val pr = {
            val pr = PasswordResetEntity(
              userId = user.id.get,
              `type` = PasswordResets.Type.email,
              hash = Some(SecureUtils.generateMD5Token(s"reset mail"))
            )
            found = true
            pr.copy(id = Some(PasswordResets.insert(pr)))
          }
          Emailer.sendPasswordReset(user, pr)
        }
        p.phone.flatMap(Users.findByPhone).foreach {user =>
          val pr = {
            val pr = PasswordResetEntity(
              userId = user.id.get,
              `type` = if (p.asSms.getOrElse(true)) PasswordResets.Type.phoneSms else PasswordResets.Type.phoneCall,
              hash = Some(Iterator.continually(r.nextInt(10)).take(8).mkString(""))
            )
            pr.copy(id = Some(PasswordResets.insert(pr)))
          }

          pr.`type` match {
            case PasswordResets.Type.phoneCall =>
              Twilio.makeCall(user, pr)
            case PasswordResets.Type.phoneSms =>
              Twilio.sendSMS(user, pr)
          }
          found = true
        }
      }

      if (found) {
        Accepted(Marshallers.ok())
      } else {
        val body = Marshallers.ko("User is not found")
        BadRequest(body)
      }
    } catch {
      case e: Throwable =>
        log.info(s"exception => ", e)
        throw e
    }
  }
  post("/reset_password", operation(resetPassword)) {
    try {
      val p = parsedBody.extract[ResetPassword]
      val pr = PasswordResets.
        // find PasswordReset
        findByHash(p.code).
        // filter inactive (already used) and too old
        filter(v => v.isActive && v.created.after(new Timestamp(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(3))))

      pr.foreach { pr =>
        DB.write { implicit session =>
          Users.findById(pr.userId).foreach { user =>
            Users.update(user.copy(password = Users.encryptPassword(p.password)))
            log.info(s">>>>> user password was reset")
          }
          PasswordResets.update(pr.copy(isActive = false))
          log.info(s">>>>> password reset object was deactivated")
        }
      }
    } catch {
      case e: Throwable =>
        log.info(s"exception => ", e)
        throw e
    }
  }
  post("/check_code", operation(checkCode)) {
    val p = parsedBody.extract[PhoneCode]
    PasswordResets.findByHash(p.code).
      filter(v => v.isActive && v.created.after(new Timestamp(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5)))).
      filter(v => v.`type` == PasswordResets.Type.phoneCall || v.`type` == PasswordResets.Type.phoneSms).map {pr =>

      val newPr = pr.copy(hash = Some(SecureUtils.generateMD5Token(s"reset mail")))
      DB.write{ implicit session => PasswordResets.update(newPr) }
      Marshallers.code(newPr.hash.get)
    }.getOrElse(NotFound)

  }
}
object UsersController {
  case class RestorePassword(userid: Option[String], phone: Option[String], asSms: Option[Boolean])
  case class ResetPassword(code: String, password: String)
  case class PhoneCode(code: String)
}

