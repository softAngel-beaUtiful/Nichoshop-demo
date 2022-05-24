package com.nichoshop.services

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.model.dto.{SessionDto => PublicSession, TokenDto => PublicToken, UserDto => PublicUser}
import com.nichoshop.models
import com.nichoshop.models.SessionEntity
import com.nichoshop.models.helpers.DB
import com.nichoshop.utils.{Conversions, Cookies}
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.LoggerFactory

import javax.servlet.http.{HttpServletRequest, HttpServletResponse, HttpSession}

class AuthService() {
  val log = LoggerFactory.getLogger(getClass)

  private def getTPermissions(user: models.UserEntity):List[models.TPermission] = {
    DB.read {implicit session => models.TPermissions.query.filter(_.userId === user.id).list}
  }
  private def getPermissions(user: models.UserEntity):List[models.PermissionEntity] = {
    DB.read {implicit session => models.Permissions.query.filter(_.userId === user.id).list}
  }
  def login(login: String, password: String): Option[PublicUser] = {
    (models.Users.findByEmailOrUserid(login) flatMap { user =>
      if (BCrypt.checkpw(password, user.password)) Some(user)
      else None
    }).map {user =>
      Conversions.toUser(user, getTPermissions(user), getPermissions(user))
    }
  }

  def saveSession(session: models.SessionEntity): Unit = DB.write { implicit s =>
    models.Sessions.insert(session)
  }

  def saveSession(userId: String, sessionId: String): Unit = {
    DB.write { implicit s =>
      models.Sessions.insert(
        SessionEntity(None, userId, sessionId, System.currentTimeMillis)
      )
    }
  }

  def dropSession(hash: String): Unit = {
    models.Sessions.deleteByHash(hash)
  }

  def fromSession(s: String): Option[PublicUser] = DB.read {implicit session =>
    log.info(s"requested fromSession")
    (for {
      user <- models.Users.query
      s1 <- models.Sessions.query
      if ( user.userid === s1.userid ) && (s1.hash === s)
    } yield user).firstOption.map(u => Conversions.toUser(u, getTPermissions(u), getPermissions(u)))
  }

  def toSession(u: PublicUser): Option[PublicSession] = DB.read { implicit session =>
    (for {
      user <- models.Users.query
      s <- models.Sessions.query
      if ( user.userid === s.userid ) && (user.userid === u.getUserid)
    } yield s).firstOption.map(Conversions.toSession)

  }

  def getUserByTokenHash(hash: String): Option[PublicUser] = DB.read { implicit  session =>
    (for {
      token <- models.Tokens.query
      user <- models.Users.query
      if (token.userid === user.userid) && (token.hash === hash)
    } yield user).firstOption.map(u => Conversions.toUser(u, getTPermissions(u), getPermissions(u)))
  }

  def getUserByTokenHashSession(hashSession: String): Option[PublicUser] = DB.read { implicit  session =>
    (for {
      token <- models.Tokens.query
      user <- models.Users.query
      if (token.userid === user.userid) && (token.hashSession === hashSession)
    } yield user).firstOption.map( u => Conversions.toUser(u, getTPermissions(u), getPermissions(u)))
  }

  def getTokenByHashSession(hashSession: String): Option[PublicToken] =
    models.Tokens.findByHashSession(hashSession).map(Conversions.toToken)

  //  }models.Tokens.fromToken(hash).map(Conversions.toUser)

//  def toToken(user: MUser): Option[Token] = userDAO.toToken(user)

  def captureRequired(key: String) = true

  def failedLoginCounterInc(key: String): Unit = {}

  def withUser(handler: PublicUser => ( () => Any ))(implicit session: HttpSession, request: HttpServletRequest, response: HttpServletResponse, unauthorizedProcessing: () => Any) = {
    Services.authService.fromSession(session.getId).
      orElse {
      Cookies.withTokenCookie(Services.authService.getUserByTokenHashSession)
    }.map(handler(_)()).getOrElse {
      unauthorizedProcessing()
    }
  }
}
