package com.nichoshop

import akka.actor.ActorSystem
import akka.util.Timeout
import com.nichoshop.Client.ProfileConfig
import com.nichoshop.Helper._
import org.json4s.DefaultFormats
import spray.client.pipelining._
import spray.http.{ContentTypes, HttpHeaders, HttpEntity, HttpRequest}
import spray.httpx.encoding.Deflate

import scala.concurrent.{Await, Future}
import scala.language.postfixOps

/**
 * Created by Evgeny Zhoga on 12.12.15.
 */
class Client(config: ProfileConfig)(implicit system: ActorSystem) {
  implicit val timeout = {
    import scala.concurrent.duration._

    Timeout(10 seconds)
  }
  import system.dispatcher
  implicit val formats = DefaultFormats

  def pipelineWithSession(implicit session: RequestsSession): HttpRequest => Future[String] = (
    withSession
      ~> sendReceive
      ~> decode(Deflate)
      ~> unmarshal[String]
    )

  def addRole(role: String)(bodyProcessor: String => Unit = Console.println)(implicit session: RequestsSession): Unit = {
    val response: Future[String] = pipelineWithSession(session)(Post(s"http://localhost:8080/api/admin/users/${config.userId}/roles" ).withEntity(
      HttpEntity(
        HttpHeaders.`Content-Type`(ContentTypes.`application/json`).contentType,
        s"""{"name": "$role"}""")
    )
    )

    val res = Await.result(response, timeout.duration)

    bodyProcessor(res)
  }

  def addPermission(permission: String)(bodyProcessor: String => Unit = Console.println)(implicit session: RequestsSession) {
    val response: Future[String] = pipelineWithSession(session)(Post(s"http://localhost:8080/api/admin/subadmins/${config.userId}/permissions" ).withEntity(
      HttpEntity(
        HttpHeaders.`Content-Type`(ContentTypes.`application/json`).contentType,
        s"""{"name": "$permission"}""")
    )
    )

    val res = Await.result(response, timeout.duration)

    bodyProcessor(res)
  }

  def getInventory(bodyProcessor: String => Unit = Console.println)(implicit session: RequestsSession) = {
    val response: Future[String] = pipelineWithSession(session)(Get("http://localhost:8080/api/seller/inventory/list?condition=all"))

    val loginInfoStr = Await.result(response, timeout.duration)

    bodyProcessor(loginInfoStr)
  }

  def stop() = system.shutdown()
}

object Client {
  case class ProfileConfig(userId: Int = 22, login: String = "test", password: String = "test")
}
