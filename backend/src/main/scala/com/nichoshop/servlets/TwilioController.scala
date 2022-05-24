package com.nichoshop.servlets

import com.nichoshop.models.PasswordResets
import com.nichoshop.services.util.Twilio
import com.nichoshop.servlets.swagger.TwilioOperations
import org.scalatra.swagger.Swagger
import org.scalatra.{Ok, ScalatraServlet}
import org.slf4j.LoggerFactory

import java.sql.Timestamp
import java.util.concurrent.TimeUnit

/**
 * Created by Evgeny Zhoga on 14.06.15.
 */
class TwilioController(implicit val swagger: Swagger) extends ScalatraServlet with TwilioOperations {
  val log = LoggerFactory.getLogger(getClass)

  get("/:hash", operation(twilioSay)) {
    val response = PasswordResets.findByHash(params("hash")).
      filter(v => v.isActive && v.created.after(new Timestamp(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5)))).
      filter(v => v.`type` == PasswordResets.Type.phoneCall && v.hash.isDefined).
      map {pr =>

//        DB.write {implicit session => PasswordResets.update(pr.copy(isActive = false))}

        Twilio.twilioSayUrl(pr.hash.get)
      }.getOrElse (Twilio.twilioSayExpired)
    Ok(body = response, headers = Map("Content-type" -> "application/xml"))
  }
}
