package com.nichoshop.servlets

import com.nichoshop.marshalling.Marshallers
import com.nichoshop.services.Services
import com.nichoshop.servlets.swagger.SessionOperations
import com.nichoshop.utils.{CapchaClient, Cookies}
import org.scalatra.swagger.Swagger
import org.slf4j.LoggerFactory

import javax.servlet.http.HttpServletRequest
import scala.util.control.NonFatal

class SessionsController(implicit val swagger: Swagger) extends AuthServlet with Json with SessionOperations {
  val log = LoggerFactory.getLogger(getClass)

  post("/", operation(login)) {
    // parsedBody.extract[LoginParams]
    //grecaptcha
    val ip = getIpFromRequest
    val captcha = true // !Services.auth.captureRequired(ip) || params.get("grecaptcha").exists(CapchaClient.checkCaptcha)
    if (captcha) try {
      userOption.
        orElse(scentry.authenticate(AuthStrategies.BasicAuth.toString)).
        map(Marshallers.toJson).
        getOrElse(halt({
        Services.authService.failedLoginCounterInc(ip)
        unauthorized("Authentication failed", capchaRequired = Services.authService.captureRequired(ip))
      }))
    } catch {
      case NonFatal(e) =>
        log.error(">> exception.. here?", e)
        throw e
    }
    else {
      halt(unauthorized("Capcha check failed", capchaRequired = Services.authService.captureRequired(ip)))
    }
  }

  post("/login", operation(loginWithUserNameAndPassword)) {
    val ip = getIpFromRequest
    val captcha = true // !Services.auth.captureRequired(ip) || params.get("grecaptcha").exists(CapchaClient.checkCaptcha)

    if (captcha) try {
      userOption.
        orElse(scentry.authenticate(AuthStrategies.UserPassword.toString)).
        map(Marshallers.toJson).
        getOrElse(halt({
          Services.authService.failedLoginCounterInc(ip)
          unauthorized("Authentication failed", capchaRequired = Services.authService.captureRequired(ip))
        }))
    } catch {
      case NonFatal(e) =>
        log.error(">> exception.. here?", e)
        throw e
    }
    else {
      halt(unauthorized("Captcha check failed", capchaRequired = Services.authService.captureRequired(ip)))
    }
  }

  get("/status", operation(loginStatus)) {
    val ip = getIpFromRequest

    Services.authService.fromSession(session.getId).
      orElse {
      Cookies.withTokenCookie(Services.authService.getUserByTokenHashSession)
    }.map(Marshallers.toJson).
      getOrElse(halt(unauthorized("Not logged in",capchaRequired = {
      val r = Services.authService.captureRequired(ip)

      log.info(s"ip = $ip, captureRequired = $r")

      r
    })))
  }

  post("/logout", operation(logout)) {
    scentry.logout()
    Marshallers.ok()
  }

  private def unauthorized(message: String, capchaRequired: Boolean = true) = {
    Marshallers.unauthorized(message, Map("grecaptcha-required" -> capchaRequired.toString))
  }
}
