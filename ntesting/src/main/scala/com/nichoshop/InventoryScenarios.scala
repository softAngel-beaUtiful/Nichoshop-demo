package com.nichoshop

import akka.actor.ActorSystem
import com.nichoshop.Client.ProfileConfig
import com.nichoshop.Helper._

import scala.util.control.NonFatal

/**
 * Created by Evgeny Zhoga on 12.12.15.
 */
object InventoryScenarios extends App {

  implicit val system = ActorSystem()

  val profileConfig = ProfileConfig()
  implicit val config: Config = Config()
  implicit val session = sessionCookie(profileConfig.login, profileConfig.password)

  val client = new Client(profileConfig)

  def t(f: => Unit) = {
    try {
      f
    } catch {
      case NonFatal(e) =>
        Console.println(s"Exception: ${e.getMessage}")
        e.printStackTrace()
    }
  }

//  t(client.addRole("SELLER")())
  val dp: (String => Unit) = { body =>
    import org.json4s._
    import org.json4s.jackson.JsonMethods._

    try {
      val json = parse(body)
      Console.println(pretty(render(json)))
    } catch {
      case NonFatal(e) =>
        Console.println(body)
    }
  }

  t(client.getInventory(dp))



  client.stop()
}
