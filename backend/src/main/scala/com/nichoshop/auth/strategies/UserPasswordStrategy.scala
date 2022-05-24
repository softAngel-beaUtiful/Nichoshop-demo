package com.nichoshop.auth.strategies

import com.nichoshop.model.dto.UserDto
import com.nichoshop.models.SessionEntity
import com.nichoshop.services.Services
import org.scalatra.ScalatraBase
import org.scalatra.auth.ScentryStrategy
import org.slf4j.LoggerFactory

import javax.servlet.http.{HttpServletRequest, HttpServletResponse, HttpSession}

class UserPasswordStrategy(override protected val app: ScalatraBase)(implicit request: HttpServletRequest, response: HttpServletResponse)
  extends BaseUserPasswordStrategy(app) {
}

