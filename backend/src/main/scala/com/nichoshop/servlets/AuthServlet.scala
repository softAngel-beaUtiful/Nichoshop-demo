package com.nichoshop.servlets

import com.nichoshop.auth.AuthenticationSupport
import com.nichoshop.exceptions.NotAuthorizedException
import com.nichoshop.services.Services
import org.scalatra.{Forbidden, ScalatraServlet}

import javax.servlet.http.HttpServletRequest

abstract class AuthServlet extends ScalatraServlet with AuthenticationSupport with ExceptionsHandler {

  def ifEnoughRights(block: => Any) = {
    if (Services.authService.fromSession(session.getId).exists(_.getId == params("id").toInt)) block
    else Forbidden("Insufficient rights")
  }

  def uid = Services.authService.fromSession(session.getId).map(_.getId).getOrElse(throw new NotAuthorizedException)

  def getIpFromRequest(implicit request: HttpServletRequest): String = {
    val ip = request.getHeader("X-FORWARDED-FOR")
    if (ip == null) {
      request.getRemoteAddr
    } else ip
  }
}
