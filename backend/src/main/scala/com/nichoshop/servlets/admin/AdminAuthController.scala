package com.nichoshop.servlets.admin

import com.nichoshop.exceptions.{AuthenticationException, UserNotFoundException}
import com.nichoshop.marshalling.Marshallers
import com.nichoshop.marshalling.Marshallers.{data, ok}
import com.nichoshop.services.duo.DuoService
import com.nichoshop.services.{AuthService, Services, UsersService}
import com.nichoshop.servlets.swagger.admin.AdminAuthOperations
import com.nichoshop.servlets.{AuthServlet, Json}
import org.scalatra.swagger.Swagger
import org.slf4j.{Logger, LoggerFactory}

import javax.servlet.http.HttpSession

class AdminAuthController(implicit val swagger: Swagger) extends AuthServlet with Json with AdminAuthOperations {
  val log: Logger = LoggerFactory.getLogger(getClass)
  val duoService: DuoService = Services.duoService
  val usersService: UsersService = Services.usersService
  val authService: AuthService = Services.authService

  post("/login-with-prompt", operation(loginWithUserNameAndPassword2FA))(recoverable {
    val ip = getIpFromRequest

    val captcha = true // !Services.auth.captureRequired(ip) || params.get("grecaptcha").exists(CapchaClient.checkCaptcha)
    if (captcha)
      userOption.
        orElse(scentry.authenticate(AuthStrategies.UserPassword2FA.toString)).
        map(user => {
          if (!usersService.hasAdminOrCustomerSupportRole(user)) {
            throw new AuthenticationException("ALLOW_LOGIN_ONLY_ADMIN_OR_CUSTOMER_SUPPORT")
          }

          data(duoService.createAuthUrl(user.getUserid))
        }).
        getOrElse(halt({
          authService.failedLoginCounterInc(ip)
          unauthorized("Authentication failed", capchaRequired = authService.captureRequired(ip))
        }))
    else {
      halt(unauthorized("Captcha check failed", capchaRequired = authService.captureRequired(ip)))
    }
  })

  post("/login-with-prompt/confirm", operation(confirmUserNameAndPassword2FA))(recoverable {
    val state = params("state")

    duoService.validateAuthorizationWithPrompt(
      duoCode = params("duo_code"),
      state = state
    )

    duoService.getEmailOrUserIdByState(state) match {
      case Some(emailOrUserId) =>
        usersService.findByEmailOrUserid(emailOrUserId) match {
          case Some(user) =>
            val httpSession: HttpSession = request.getSession(true)
            authService.saveSession(user.userid, httpSession.getId)
            duoService.deleteStateCacheRecord(state)

          case None => throw new UserNotFoundException()
        }
      case None => throw new UserNotFoundException()
    }

    ok()
  })

  post("/logout", operation(logout)) {
    scentry.logout()
    Marshallers.ok()
  }

  private def unauthorized(message: String, capchaRequired: Boolean = true) = {
    Marshallers.unauthorized(message, Map("grecaptcha-required" -> capchaRequired.toString))
  }
}
