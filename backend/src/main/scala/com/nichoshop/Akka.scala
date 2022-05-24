package com.nichoshop

import akka.actor.ActorSystem

/**
 * Created by Evgeny Zhoga on 24.10.15.
 */
object Akka {
  val system = ActorSystem("nichoshop-main", Environment.config)
}
