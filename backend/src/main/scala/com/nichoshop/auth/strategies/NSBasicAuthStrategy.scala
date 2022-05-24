package com.nichoshop.auth.strategies

import com.nichoshop.model.dto.UserDto
import com.nichoshop.models.SessionEntity
import com.nichoshop.services.Services
import org.scalatra.ScalatraBase
import org.scalatra.auth.strategy.BasicAuthStrategy
import org.slf4j.LoggerFactory

import javax.servlet.http.{HttpServletRequest, HttpServletResponse, HttpSession}

/**
 * Created by Evgeny Zhoga on 02.06.15.
 */
class NSBasicAuthStrategy(override protected val app: ScalatraBase) extends BasicAuthStrategy[UserDto](app, "nichoshop") {
  val log = LoggerFactory.getLogger(getClass)

  override protected def getUserId(user: UserDto)(implicit request: HttpServletRequest, response: HttpServletResponse): String = user.getUserid

  override protected def validate(userName: String, password: String)(implicit request: HttpServletRequest, response: HttpServletResponse): Option[UserDto] = Services.authService.login(userName, password) match {
    case u@Some(_) =>
      log.info(s"NSBasicAuthStrategy: login succeeded for $userName")
      u
    case None =>
      log.info(s"NSBasicAuthStrategy: login failed for $userName")
      None
  }

  override def afterAuthenticate(winningStrategy: String, user: UserDto)(implicit request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val httpSession: HttpSession = request.getSession(true)
    val session = SessionEntity(None, user.getUserid, httpSession.getId, System.currentTimeMillis)  //todo false or true??
    Services.authService.saveSession(session)
  }

  override def unauthenticated()(implicit request: HttpServletRequest, response: HttpServletResponse): Unit = {
      log.info(s" >>> unauthenticated")
  }

  override def beforeLogout(user: UserDto)(implicit request: HttpServletRequest, response: HttpServletResponse): Unit = {
    Services.authService.dropSession(request.getSession(false).getId)
  }
}
