package com.nichoshop.servlets.swagger

import com.nichoshop.legacy.models.UsersRow
import org.scalatra.swagger.SwaggerSupport

trait SignInOperations extends SwaggerSupport {

  def name: String = "Signin"

  override protected def applicationDescription: String = "Signin"  

  val signin = (apiOperation[UsersRow]("signin")
    summary "Signin"
    parameters(formParam[String]("signin").description("userid or email"),
    formParam[String]("password").description("password"),    
    formParam[String]("grecaptcha").description("recapcha v2 value")))

  val logout = (apiOperation[UsersRow]("signout")
    summary "Signout")

  val addPhone = (apiOperation[Int]("addPhone")
    summary s"Add Phone Number"
    notes "example +44 1234567890"
    parameter pathParam[String]("phone").description("Mobile Phone Number with country code"))
}
