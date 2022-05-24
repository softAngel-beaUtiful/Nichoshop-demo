package com.nichoshop.servlets.customer

import com.nichoshop.auth.ControllerAccess
import com.nichoshop.marshalling.Marshallers
import com.nichoshop.services.Services
import org.scalatra.ScalatraServlet

import javax.servlet.http.HttpServletRequest

/**
 * Created by Evgeny Zhoga on 31.08.15.
 */
abstract class CustomerController extends ScalatraServlet {
  implicit val accessType = ControllerAccess.CUSTOMER

  implicit def notAuthorized(implicit request: HttpServletRequest): () => Nothing =
    () => halt(unauthorized("Not logged in",capchaRequired = Services.authService.captureRequired(ip)))

  private def unauthorized(message: String, capchaRequired: Boolean = true) = {
    Marshallers.unauthorized(message, Map("grecaptcha-required" -> capchaRequired.toString))
  }
  private def ip(implicit request: HttpServletRequest) = {
    val ip = request.getHeader("X-FORWARDED-FOR")
    if (ip == null) {
      request.getRemoteAddr
    } else ip
  }

  protected def mark(name: String, action: CustomerController.Action): Unit = {

  }
  protected def markEnter(name: String) = mark(name, CustomerController.Enter)
  protected def markExit(name: String) = mark(name, CustomerController.Exit)
}

object CustomerController {
  sealed trait Action {
    def name: String
  }
  case object Enter extends Action {
    val name = "ented"
  }
  case object Exit extends Action {
    val name = "exit"
  }

}
