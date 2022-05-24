package com.nichoshop.services.util

import com.nichoshop.Environment
import com.nichoshop.models.{PasswordResetEntity, UserEntity}
import com.twilio.sdk.TwilioRestClient
import com.twilio.sdk.verbs.{Say, TwiMLResponse}
import org.apache.http.message.BasicNameValuePair
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

/**
 * Created by Evgeny Zhoga on 14.06.15.
 */
object Twilio {
  private val log = LoggerFactory.getLogger(getClass)
  import com.nichoshop.Environment._
  val client = new TwilioRestClient(twilio.accountSid, twilio.authToken)

  val params = List(
    new BasicNameValuePair("From", twilio.from)
  )
  def makeCall(user: UserEntity, pr: PasswordResetEntity): Unit = {
    val to = new BasicNameValuePair("To", s"+${user.phone.get}")
//    val url = new BasicNameValuePair("Url", s"http://nichoshop.com/api/twilio/${pr.hash.get}")
    val url = new BasicNameValuePair("Url", s"${Environment.hostWithRootPath}/api/twilio/${pr.hash.get}")
    log.info(s" >>>>>>>>>> will call with url ${url.getValue}")

    // val callFactory = client.getAccount.getCallFactory
    // val call = callFactory.create(to :: url :: params)

    // log.info(s"call sid: ${call.getSid}")
  }

  def sendSMS(user: UserEntity, pr: PasswordResetEntity): Unit = {
    // val to = new BasicNameValuePair("To", s"+${user.phone.get}")
    // val body = new BasicNameValuePair("Body", s"You code is: ${pr.hash.get}")
    // log.info(s" >>>>>>>>>> will sms with code ${pr.hash.get}")

    // val smsFactory = client.getAccount.getSmsFactory
    // val sms = smsFactory.create(to :: body :: params)

    // log.info(s"sms sid: ${sms.getSid}")
  }

  def sendSMSToNumber(number: String, code: String): Unit = {

    val to = new BasicNameValuePair("To", s"+${number}")
    val body = new BasicNameValuePair("Body", s"You code is: ${code}")
    log.info(s" >>>>>>>>>> will sms with code ${code}")

    val smsFactory = client.getAccount.getSmsFactory
    val sms = smsFactory.create(to :: body :: params)

    log.info(s"sms sid: ${sms.getSid}")

  }

  def toNumericCode(hash: String, length: Int = 8) = hash.map( s => s ).mkString(" ")
    //hash.take(math.min(length, hash.length)).map(c => (c - '\00') % 10).mkString(" ")

  def twilioSayUrl(hash: String) = {
    val response = new TwiMLResponse()
    val say = new Say(s"Your code is: ${toNumericCode(hash)}") {
      setVoice("man")
      setLoop(3)
    }
    response.append(say)
    response.toEscapedXML
  }
  def twilioSayExpired = {
    val response = new TwiMLResponse()
    val say = new Say("Session expired!") {
      setVoice("man")
    }
    response.append(say)
    response.toEscapedXML
  }
}
