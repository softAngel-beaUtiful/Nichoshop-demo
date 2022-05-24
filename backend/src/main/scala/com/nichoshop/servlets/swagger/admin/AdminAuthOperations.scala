package com.nichoshop.servlets.swagger.admin

import com.nichoshop.legacy.models.UsersRow
import org.scalatra.swagger.{SwaggerSupport, SwaggerSupportSyntax}

trait AdminAuthOperations extends SwaggerSupport {

  def name: String = "Admin Auth Operations"

  override protected def applicationDescription: String = "Admin Auth"

  val loginWithUserNameAndPassword2FA: SwaggerSupportSyntax.OperationBuilder = (
    apiOperation[String]("loginWithUserNameAndPassword2FA")
      summary "Two-Factor authentication to site with username and password"
      tags "Admin Auth"

      parameters(
      queryParam[String]("login").description("userId or email"),
      queryParam[String]("password").description("password"),
      queryParam[String]("grecaptcha").description("grecaptcha").optional)
    )

  val confirmUserNameAndPassword2FA: SwaggerSupportSyntax.OperationBuilder = (
    apiOperation[String]("confirmUserNameAndPassword2FA")
      summary "Confirm Two-Factor authentication to site with username and password"
      tags "Admin Auth"

      parameters(
      queryParam[String]("duo_code").description("DUO code"),
      queryParam[String]("state").description("state"))
    )

  val logout: SwaggerSupportSyntax.OperationBuilder = (
    apiOperation[UsersRow]("logout")
      summary "Logout from site"
      tags "Admin Auth"
    )
}
