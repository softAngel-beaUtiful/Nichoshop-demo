package com.nichoshop.servlets

import com.nichoshop.Environment
import com.nichoshop.legacy.models.{EmailConfirmationRow, UsersRow}
import com.nichoshop.marshalling.Marshallers
import com.nichoshop.model.AccountType
import com.nichoshop.models.UserEntity
import com.nichoshop.services.UserService
import com.nichoshop.services.util.Emailer
import com.nichoshop.servlets.swagger.SignUpOperations
import com.nichoshop.utils.CapchaClient
import com.nichoshop.utils.auth.SecureUtils
import org.json4s.JsonAST.{JBool, JObject, JString}
import org.scalatra.swagger.Swagger
import org.scalatra.{Ok, ScalatraServlet}
import org.slf4j.LoggerFactory

import java.util.Optional
import scala.util.control.NonFatal

class SignUpController(userService: UserService)
                      (implicit val swagger: Swagger)
  extends ScalatraServlet with Json with SignUpOperations with ExceptionsHandler {

  private val log = LoggerFactory.getLogger(getClass)

  post("/", operation(signUp))(recoverable {

    val signUpUser = parsedBody.extract[SignUpUser]

    //val capcha = Environment.skipCaptcha || CapchaClient.checkCaptcha(signUpUser.grecaptcha)

    //if (capcha) {
      val user = signUpUser.toUsersRow

      userService.create(user)

      val id = userService.findByUserId(user.userid).get.id //todo: check bad case when user hadn't been saved

      val code = SecureUtils.generateMD5Token(user.userid)

      userService.saveEmailConfirmationCode(EmailConfirmationRow(id, code))
      Emailer.sendEmailConfirmation(signUpUser.toUserEntity, code)

      compact(new JObject(
        List(
          Some("success" -> JBool(value = true)),
          Some("email" -> JString(user.email)),
          Some("confirmation_code" -> JString(code)).filter(_ => Environment.isTesting)
        ).flatten
      ))

    //} else {
    //  Marshallers.forbidden("Capcha check failed")
    //}
  })

  get("/confirm_email/:code", operation(confirmEmail)) {
    val result = userService.confirmEmail(params("code"))
    if (result) {
      compact(new JObject(
        List(
          Some("success" -> JBool(value = true))
        ).flatten
      ))
    } else {
      Marshallers.bad("Email is not confirmed!")
    }
  }

  get("/check_email/:email", operation(checkEmail)) {
    compact(new JObject(List(
      "exists" -> JBool(userService.checkEmailAlreadyExists(params("email")))
    )))
  }
  get("/check_userid/:userid", operation(checkUid)) {
    compact(new JObject(List(
      "exists" -> JBool(userService.checkUseridAlreadyExists(params("userid")))
    )))
  }

}

case class SignUpUser(userid: String, fname: String, lname: String, email: String, password: String, grecaptcha: String, phone: Option[String], accountType: AccountType) {

  def toUserEntity: UserEntity =
    UserEntity(
      userid = userid,
      password = password,
      email = email,
      name = fname,
      lname = lname,
      accountType = accountType
    )

    def toUsersRow: UsersRow =
      UsersRow(0, userid, password, email, fname, lname, System.currentTimeMillis(), accountType = Option(accountType.toString))
  }
