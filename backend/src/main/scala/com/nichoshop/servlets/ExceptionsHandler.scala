package com.nichoshop.servlets

import com.nichoshop.exceptions.{AuthSessionExpiredOrNotExistsException, NotAuthorizedException}
import org.json4s.JsonAST.{JObject, JString}
import org.json4s.jackson.JsonMethods._
import org.scalatra.Control
import org.slf4j.LoggerFactory

import scala.language.implicitConversions
import scala.util.control.NonFatal

trait ExceptionsHandler extends Control {
  private val log = LoggerFactory.getLogger(getClass)

  implicit def recoverable(action: => Any): Any = try {
    action
  } catch {
    case NonFatal(e: NotAuthorizedException) =>
      log.error("exception happens: ", e)
      halt(401, compact(JObject(List("error" -> JString(e.getMessage)))))
    case NonFatal(e: AuthSessionExpiredOrNotExistsException) =>
      log.error("exception happens: ", e)
      halt(401, compact(JObject(List("error" -> JString(e.getMessage)))))
    case NonFatal(e) =>
      log.error("exception happens: ", e)
      halt(400, compact(JObject(List("error" -> JString(e.getMessage)))))
  }
}
