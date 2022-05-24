package com.nichoshop.utils

import com.nichoshop.Environment
import org.apache.commons.io.IOUtils
import org.apache.http.HttpResponse
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.config.SocketConfig
import org.apache.http.conn.ConnectionKeepAliveStrategy
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair
import org.apache.http.protocol.HttpContext
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory

import java.util.concurrent.TimeUnit
import scala.collection.JavaConversions._

/**
 * Created by Evgeny Zhoga on 06.06.15.
 */
object CapchaClient {
  val log = LoggerFactory.getLogger(getClass)
  private val client =
    HttpClientBuilder.create().setDefaultSocketConfig(
      SocketConfig.custom().
        setSoKeepAlive(true).
        setTcpNoDelay(true).
        setSoTimeout(TimeUnit.MINUTES.toMillis(5).toInt).
        build()).
      setDefaultRequestConfig(
        RequestConfig.custom().
          setConnectTimeout(500).
          build()).
      setKeepAliveStrategy(
        new ConnectionKeepAliveStrategy {
          def getKeepAliveDuration(response: HttpResponse, context: HttpContext) = TimeUnit.SECONDS.toMillis(30)
        }).
      build()

  /**
   * https://developers.google.com/recaptcha/docs/
   *
   * @param userResponse
   * @return
   */
  def checkCaptcha(userResponse: String) = {
    val method = new HttpPost("https://www.google.com/recaptcha/api/siteverify") {
      setEntity(new UrlEncodedFormEntity(
        List(
          new BasicNameValuePair("secret", Environment.recapcha),
          new BasicNameValuePair("response", userResponse)
        )
      ))
    }
    val s = client.execute(method)
    Option(s.getEntity).exists { entity =>
      val inputStream = entity.getContent
      try {
        import org.json4s._
        import org.json4s.jackson._

        val response: JsonInput = IOUtils.toString(inputStream)
        log.info(s">>>>>> capcha response: [$response]")
        val t = JsonMethods.parse(response)
        t \ "success" match {
          case JBool(v) => v
          case _ => false
        }
      } finally {
        EntityUtils.consumeQuietly(s.getEntity)
        inputStream.close()
      }

    }

  }
}
