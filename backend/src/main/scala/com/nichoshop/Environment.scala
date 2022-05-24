package com.nichoshop

import com.nichoshop.utils.db.DataSource
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import java.util.Properties
import scala.slick.driver.JdbcDriver


/**
 * Created by Evgeny Zhoga on 01.06.15.
 */
object Environment {
  private val log = LoggerFactory.getLogger(getClass)
  val config = ConfigFactory.load("application.conf")
  val isStable = config.getString("environment.type") == "production" ||
    config.getString("environment.type") == "stable"

  val isTesting = !isStable

  val driver: JdbcDriver =
      if (config.getString("db.type") == "mysql")scala.slick.driver.MySQLDriver
      else scala.slick.driver.HsqldbDriver

  val db = {
    import driver.simple._

    import scala.collection.JavaConversions._
    val p = new Properties {
      config.getConfig(s"${config.getString("db.type")}.db").
        entrySet().
        foreach ( entry => setProperty(entry.getKey, entry.getValue.unwrapped().toString) )
    }
    Database.forDataSource(DataSource.create(p))
  }

  val recapcha =
    if (isStable) Environment.config.getString("recaptcha.stable.secret")
    else Environment.config.getString("recaptcha.test.secret")

  val skipCaptcha = config.getBoolean("recaptcha.skip")

  val host = config.getString("nichoshop.host")
  val hostWithRootPath = config.getString("nichoshop.protocol") + "://" + host + config.getString("nichoshop.root-path")
//    if (isStable) "http://www.nichoshop.com"
//    else "http://localhost:8080"

  object twilio {
    val accountSid =
      if (isStable) config.getString("twilio.stable.accountSid")
      else config.getString("twilio.test.accountSid")

    val authToken =
      if (isStable) config.getString("twilio.stable.authToken")
      else config.getString("twilio.test.authToken")

    val from =
      if (isStable) config.getString("twilio.stable.from")
      else config.getString("twilio.test.from")
  }

  object duo {
    val clientId: String = config.getString("duo.web.sdk.client-id")
    val clientSecret: String = config.getString("duo.web.sdk.client-secret")
    val apiHost: String = config.getString("duo.web.sdk.api-host")
    val redirectUri: String = config.getString("duo.web.sdk.redirect-uri")
  }

  object recaptcha {
    object v2 {
      val uri: String = config.getString("recaptcha.v2.uri")
      val secret: String = config.getString("recaptcha.v2.secret")
    }
  }

  log.info("Environment setup: " + (if (isStable) "stable" else "testing"))
}
