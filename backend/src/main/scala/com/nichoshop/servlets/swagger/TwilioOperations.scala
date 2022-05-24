package com.nichoshop.servlets.swagger

import com.nichoshop.legacy.models.{Manifests, UsersRow}

trait TwilioOperations extends ApiDescription[UsersRow] {
  def name = "Twilio"

  implicit def manifestForT: Manifest[UsersRow] = Manifests.user

  val twilioSay = (apiOperation[UsersRow]("logged_in")
    summary s"Handler for twilio backcall"
    tags "Twilio"
    )

}
