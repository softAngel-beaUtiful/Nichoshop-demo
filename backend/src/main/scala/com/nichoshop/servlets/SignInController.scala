package com.nichoshop.servlets

import com.nichoshop.marshalling.Marshallers
import com.nichoshop.services.{Services, UserService}
import com.nichoshop.servlets.swagger.SignInOperations
import com.nichoshop.utils.auth.SecureUtils
import org.json4s.JsonAST._
import org.scalatra.ScalatraServlet
import org.scalatra.swagger.Swagger
import org.slf4j.LoggerFactory

import javax.servlet.http.HttpServletRequest

class SignInController(userService: UserService)
                      (implicit val swagger: Swagger)
  extends ScalatraServlet with Json with SignInOperations with ExceptionsHandler {


  private val log = LoggerFactory.getLogger(getClass)

  post("/", operation(signin)) {

    def ip(implicit request: HttpServletRequest) = {
      val ip = request.getHeader("X-FORWARDED-FOR")
      if (ip == null) {
        request.getRemoteAddr
      } else ip
    }


    val signin = params("signin")
    val password = params("password")
    val grecaptcha = params("grecaptcha")

    val capcha = true //Environment.skipCaptcha || CapchaClient.checkCaptcha(grecaptcha)

    if (capcha) {
      val existEmail = userService.checkEmailAlreadyExists(params("signin"))
      val existUserId = userService.checkUseridAlreadyExists(params("signin"))

      if (existEmail || existUserId) {
        Services.authService.login(signin, password) match {
          case u@Some(usr) =>
            log.info(s"UserPasswordStrategy: login succeeded for $usr")
            Marshallers.toJson(usr)
          case None =>
            log.info(s"UserPasswordStrategy: login failed for $signin")
            Marshallers.ko(s"login failed for $signin")
        }
      } else {
        Marshallers.ko("Email or Username does not exist")
      }

    } else {
      Marshallers.forbidden("Capcha check failed")
    }
  }

  put("/addPhone/:userId", operation(addPhone)) {

    val userId = params("userId")
    val phone = params("phone")
    val code = SecureUtils.generateSMSCode()

    if (userService.checkUseridAlreadyExists(userId)) {

      val result = userService.addPhoneAndSendSMS(userId, phone, code)

      if (result) {

        // TODO: - send SMS
        //Twilio.sendSMSToNumber(phone, code)

        compact(new JObject(
          List(
            Some("success" -> JBool(value = true)),
            Some("userid" -> JString(userId)),
            Some("code" -> JString(code)),
            Some("phone" -> JString(phone))
          ).flatten
        ))
      } else {
        Marshallers.bad("Phone Number is already exist!")
      }

    } else {
      Marshallers.bad(s"User with id ${userId} does not exist!")
    }


  }
}



