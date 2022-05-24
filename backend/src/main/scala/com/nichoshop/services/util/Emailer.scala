package com.nichoshop.services.util

import com.nichoshop.Environment
import com.nichoshop.models.{PasswordResetEntity, UserEntity}

object Emailer {
  import Environment._
  def sendEmailConfirmation(user: UserEntity, code: String) = {
    //TODO
    val message =
      s"""
        |Hello ${user.name} ${user.lname},
        |
        |This email address was used to register on www.nichoshop.com
        |To confirm this address, please, click on link:
        |
        |$hostWithRootPath/#/registerConfirm?code=$code
        |
        |If you did not register on $host, ignore this message.
        |
        |
        |With best regards,
        |NichoShop Team
      """.stripMargin

    com.nichoshop.mail.Emailer.send(user.email, s"$host confirmation", message = message)
  }

  def sendPasswordReset(user: UserEntity, passwordReset: PasswordResetEntity) = {
    val message =
      s"""
         |Hello ${user.name} ${user.lname},
         |
         |password reset was requested for this email.
         |If you did not ask for that, please, ignore this message. Otherwise
         |use link below to reset your password
         |
         |$hostWithRootPath/#/reset?code=${passwordReset.hash.get}
         |
         |With best regards,
         |NichoShop Team
       """.stripMargin
    com.nichoshop.mail.Emailer.send(user.email, "nichoshop.com reset password", message = message)
  }

}
