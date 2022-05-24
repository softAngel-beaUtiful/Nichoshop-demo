package com.nichoshop.servlets.seller

import com.nichoshop.auth.ControllerAccess
import com.nichoshop.marshalling.Marshallers
import com.nichoshop.services.Services
import org.scalatra.ScalatraServlet

import javax.servlet.http.HttpServletRequest

/**
 * Created by Evgeny Zhoga on 23.08.15.
 */
abstract class SellerController extends ScalatraServlet {
  implicit val accessType = ControllerAccess.SELLER
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
}
