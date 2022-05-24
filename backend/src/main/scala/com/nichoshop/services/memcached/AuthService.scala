package com.nichoshop.services.memcached

import com.nichoshop.model.dto.{LoginDto, UserDto}
import com.nichoshop.{Environment, models}
import com.nichoshop.utils.Memcached._
import com.nichoshop.utils.{Conversions, Memcached}
import shade.memcached.Codec._

import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.{global => ec}
import scala.util.control.NonFatal

/**
 * Created by Evgeny Zhoga on 03.06.15.
 */
trait AuthService extends com.nichoshop.services.AuthService with BaseService {
  private def sessionKey(sessionId: String) = s"session:$sessionId"
  private def tokenKey(tokenId: String) = s"token:$tokenId"
  private def sessionUserKey(sessionId: String) = s"session:$sessionId:user"
  private def tokenUserKey(tokenId: String) = s"token:$tokenId:user"
  private def userKey(userId: String) = s"user:$userId"

  override def login(login: String, password: String): Option[UserDto] = {
    val user = super.login(login, password)
    user.foreach{ u =>
      set(userKey(u.getUserid), u)
    }
    user
  }

  override def saveSession(session: models.SessionEntity): Unit = {
    val s = Conversions.toSession(session)
    try {
      set(sessionKey(s.getId), s).
        map (_ => set(sessionUserKey(s.getId), s.getUserid)).
        map (_ => super.saveSession(session))
    } catch {
      case NonFatal(e) =>
        super.saveSession(session)
    }
  }


  override def dropSession(hash: String): Unit = {
    try {
      delete(sessionKey(hash))
      delete(sessionUserKey(hash))
    } finally {
      super.dropSession(hash)
    }
  }

  override def fromSession(session: String): Option[UserDto] = {
    try {
      get[String](sessionUserKey(session)).
        flatMap (userId => get[UserDto](userKey(userId))) match {
        case e@Some(_) => e
        case None =>
          log.info(s"session not found in memcached")
          if (!Environment.isStable) {
            log.info(s"try get session from DB for non-stable environment")
            super.fromSession(session)
          } else {
            None
          }
      }
    } catch {
      case NonFatal(e) =>
        log.warn(s"cannot get user from memcached =>", e)
        super.fromSession(session)
    }
  }

  private def getLogin(key: String) = {
    import Memcached.LoginBinaryCodec
    Memcached.client.awaitGet[LoginDto](s"login:$key").map(t => {
      log.info(s" >>>> found login: ${t.getCounter}")
      t
    }).filter(_.getTimestamp > System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)).getOrElse(LoginDto.newBuilder.setCounter(0).setTimestamp(0).build)
  }

  override def captureRequired(key: String) = {

    val login = getLogin(key)

    val r = login.getCounter > 2
    log.info(s" >>>>>>> capcha is required for key [$key]? $r")
    r

  }

  override def failedLoginCounterInc(key: String): Unit = {
    import Memcached.LoginBinaryCodec

    import scala.concurrent.duration._

    val login = getLogin(key)

    log.info(s" >>>>>>> increase counter for [$key]")
    val l = LoginDto.newBuilder().
      setCounter(login.getCounter + 1).
      setTimestamp(System.currentTimeMillis()).build
    Memcached.client.awaitSet(s"login:$key", l, 1 hour)
  }

}
