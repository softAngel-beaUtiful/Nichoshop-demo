package com.nichoshop

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.util.Timeout
import spray.client.pipelining._
import spray.http.{BasicHttpCredentials, HttpResponse, HttpRequest}
import spray.httpx.encoding.Deflate

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, Future}

/**
 * Created by Evgeny Zhoga on 01.12.15.
 */
object Helper {
  implicit val timeout = {
    import scala.concurrent.duration.FiniteDuration

    Timeout(FiniteDuration(10, TimeUnit.SECONDS))
  }

  def sessionCookie(user: String = "test", password: String = "test")(implicit context: ActorSystem, config: Config): RequestsSession = {
    implicit val dispatcher = context.dispatcher

    val pipeline: HttpRequest => Future[HttpResponse] = (
      addHeader("X-My-Special-Header", "fancy-value")
        ~> addCredentials(BasicHttpCredentials(user, password))
        ~> sendReceive
        ~> decode(Deflate))

    val r: Future[HttpResponse] = pipeline(Post(s"http://${config.host}:${config.port}/api/sessions"))

    val response = Await.result(r, timeout.duration)
    val session = response.headers.find(_.name == "Set-Cookie")
    RequestsSession(session.get.value)
  }
  def withSession(implicit session: RequestsSession): RequestTransformer = addHeader("Cookie", session.key)

  case class RequestsSession(key: String)
  case class Config(host: String = "localhost", port: Int = 8080)
}
