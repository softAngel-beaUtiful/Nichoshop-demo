package com.nichoshop.mail

import com.nichoshop.Environment
import org.apache.commons.mail._
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.{global => ec}
import scala.concurrent.Future
import scala.util.control.NonFatal

/**
 * Created by Evgeny Zhoga on 06.06.15.
 */
trait Emailer {
  private val log = LoggerFactory.getLogger(getClass)

  def send(to: String, subject: String, message: String) = Future {
    try {
      val config = Environment.config.getConfig("smtp")
//      log.info(config.root().render())
      val fromAddress = config.getString("from.address")
      val fromName = config.getString("from.name")
      val testEmail = config.getString("testEmail")

      val mail = new SimpleEmail().setMsg(message)

      if (Environment.isTesting) {
        mail.addTo(testEmail)
        mail.addHeader("X-NichoShop-Original-To", to)
      } else {
        mail.addTo(to)
      }

      mail.setStartTLSEnabled(config.getBoolean("tls"))
      mail.setSSLOnConnect(config.getBoolean("ssl"))
      mail.setHostName(config.getString("host"))

      mail.setSmtpPort(config.getInt("port"))
      mail.setAuthenticator(new DefaultAuthenticator(config.getString("login"), config.getString("password")))

      mail.setFrom(fromAddress, fromName)
      mail.setSubject(subject)

      mail.send()
      log.info(s"mail was sent to ${mail.getToAddresses.mkString("[", ", ", "]")}")
    } catch {
      case NonFatal(e) =>
        log.error(s"error while sending mail ===>", e)
    }
  }
}

object Emailer extends Emailer
