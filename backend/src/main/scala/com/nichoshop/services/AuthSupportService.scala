package com.nichoshop.services

import com.nichoshop.exceptions.NotAuthorizedException
import com.nichoshop.utils.Cookies

import java.util.concurrent.TimeUnit
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import scala.util.{Failure, Try}

/**
 * Created by Evgeny Zhoga on 26.06.15.
 */
class AuthSupportService {

  def required[T](state: LoginState)(body: => T)(implicit request: HttpServletRequest, response: HttpServletResponse): Try[T] = {
    val sessionIdOption = Option(request.getSession(false)).map(_.getId)
    val rememberMeOption = request.getCookies.find(_.getName == Cookies.REMEMBER_ME)

    if (state == NOT_LOGGED_IN) {
      Try(body)
    } else if (state == WEAK_TOKEN && rememberMeOption.flatMap(rm => Services.authService.getUserByTokenHash(rm.getValue)).isDefined) {
      Try(body)
    } else if (state == STRONG_TOKEN && rememberMeOption.isDefined && {
      val token = Services.authService.getTokenByHashSession(rememberMeOption.get.getValue)
      val isFresh = token.map(_.getCreated).exists(_ >= System.currentTimeMillis() - TimeUnit.HOURS.toMillis(6))
      isFresh
    }) {
      Try(body)
    } else if (state == SESSION && sessionIdOption.map(Services.authService.fromSession).isDefined) {
      Try(body)
    } else {
      Failure(new NotAuthorizedException)
    }
  }

  sealed abstract class LoginState(val priority: Int)
  case object NOT_LOGGED_IN extends LoginState(1)
  case object WEAK_TOKEN extends LoginState(2)
  case object STRONG_TOKEN extends LoginState(3)
  case object SESSION extends LoginState(4)

}
