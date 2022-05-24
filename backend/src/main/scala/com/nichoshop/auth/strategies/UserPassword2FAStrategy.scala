package com.nichoshop.auth.strategies

import com.nichoshop.model.dto.UserDto
import com.nichoshop.models.SessionEntity
import com.nichoshop.services.Services
import org.scalatra.ScalatraBase
import org.scalatra.auth.ScentryStrategy
import org.slf4j.LoggerFactory

import javax.servlet.http.{HttpServletRequest, HttpServletResponse, HttpSession}

class UserPassword2FAStrategy(override protected val app: ScalatraBase)(implicit request: HttpServletRequest, response: HttpServletResponse)
  extends BaseUserPasswordStrategy(app) {

  override def afterAuthenticate(winningStrategy: String, user: UserDto)
                                (implicit request: HttpServletRequest, response: HttpServletResponse): Unit = {}

  override def beforeLogout(user: UserDto)(implicit request: HttpServletRequest, response: HttpServletResponse): Unit = {}
}

