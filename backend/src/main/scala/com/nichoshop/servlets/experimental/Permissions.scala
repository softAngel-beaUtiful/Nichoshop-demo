package com.nichoshop.servlets.experimental

/**
 * Created by Evgeny Zhoga on 24.06.15.
 */
trait Permission

object LoginStates {
/*
  def required[T](state: LoginState)(body: => T)(implicit request: HttpServletRequest) = {
    val sessionIdOption = Option(request.getSession(false)).map(_.getId)
    val rememberMeOption = request.getCookies.find(_.getName == Cookies.REMEMBER_ME)

    if ( (state.priority <= SESSION.priority && sessionIdOption.flatMap(Services.auth.fromSession).isDefined)
      || (state.priority <= STRONG_REMEMBER_ME.priority && rememberMeOption.flatMap(Services.auth.getUserByTokenHash))
    ) {
    }
    Services.auth.fromSession()
  }
*/

  sealed abstract class LoginState(val priority: Int)
  case object NOT_LOGGED_IN extends LoginState(1)
  case object WEAK_REMEMBER_ME extends LoginState(2)
  case object STRONG_REMEMBER_ME extends LoginState(3)
  case object SESSION extends LoginState(4)
}
