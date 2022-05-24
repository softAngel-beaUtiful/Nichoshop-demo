package com.nichoshop.servlets.admin

import com.nichoshop.marshalling.Marshallers
import com.nichoshop.model.dto.{Role, UserDto}
import com.nichoshop.services.{PermissionsService, Services}
import com.nichoshop.servlets.Json
import com.nichoshop.servlets.admin.CustomerSupportController._
import com.nichoshop.servlets.customer.CustomerController
import org.json4s.JsonAST.JArray
import org.slf4j.LoggerFactory

/**
 * Created by Evgeny Zhoga on 06.12.15.
 */
class CustomerSupportController extends CustomerController with Json {
  private val log = LoggerFactory.getLogger(getClass)

  def name = "customerSupport-permissions"

  /**
   * get all customer support users
   */
  get("/") {
    try {
      Services.authService.withUser { user =>() =>
        val p = PermissionsService.Permissions.`customerSupports:get`
        Services.permissions.withProtection(p)(user) {
          val users = Services.usersService.getUserList()
          JArray(
            users.map(Marshallers.toJson)
          )
        }.getOrElse(halt(Marshallers.unauthorized(s"Permission required: ${p.name}")))
      }
    } catch {
      case e: Throwable =>
        log.error("", e)
        throw e
    }
  }

  post("/") {
    try {
      Services.authService.withUser { user =>() =>
        val p = PermissionsService.Permissions.`customerSupport:add`
        Services.permissions.withProtection(p)(user) {
          val sa = parsedBody.extract[CustomerSupport]

          Services.usersService.addUser(sa.toModel, sa.password, sa.permissions)

        }.getOrElse(halt(Marshallers.unauthorized(s"Permission required: ${p.name}")))
      }
    } catch {
      case e: Throwable =>
        log.error("", e)
        throw e
    }
  }
  /**
   * get specific customer support
   */
  get("/:userId") {
    try {
      Services.authService.withUser { user =>() =>
        val p = PermissionsService.Permissions.`customerSupport:get`
        Services.permissions.withProtection(p)(user) {
          Services.usersService.getUser(params("userId").toInt).filter(u => Option(u.getRoles).exists(_.contains(Role.CUSTOMER_SUPPORT))).map(Marshallers.toJson).
            getOrElse(halt(Marshallers.notFound("Customer support user not found")))
        }.getOrElse(halt(Marshallers.unauthorized(s"Permission required: ${p.name}")))
      }
    } catch {
      case e: Throwable =>
        log.error("", e)
        throw e
    }
  }


  post("/:userId/permissions") {
    try {
      Services.authService.withUser { user =>() =>
        val p = PermissionsService.Permissions.`permission:add`

        Services.permissions.withProtection(p)(user) {
          val perm = parsedBody.extract[Permission]

          if (Services.usersService.addPermissionToUser(params("userId").toInt, perm.name))
            Marshallers.ok()
          else
            Marshallers.unauthorized(s"Not allowed to add customer support permissions to this")
        }.getOrElse(halt(Marshallers.unauthorized(s"Permission required: ${p.name}")))
      }
    } catch {
      case e: Any =>
        log.error(e.getMessage, e)
        throw e
    }
  }

  delete("/:userId/permission/:permission") {
    try {
      Services.authService.withUser { user =>() =>
        val p = PermissionsService.Permissions.`permission:remove`

        Services.permissions.withProtection(p)(user) {
          val p = params("permission")
          Services.usersService.removePermissionFromUser(params("userId").toInt, p)
          Marshallers.ok()
        }.getOrElse(halt(Marshallers.unauthorized(s"Permission required: ${p.name}")))
      }
    } catch {
      case e: Any =>
        log.error(e.getMessage, e)
        throw e
    }
  }

}

object CustomerSupportController {
  case class Permission(name: String)
  case class CustomerSupport(fname: String, lname: String, userid: String, email: String, password: String, permissions: List[String]) {
    require(userid.toLowerCase().startsWith("s"))

    def toModel = UserDto.newBuilder().
      setUserid(userid).
      setFirstName(fname).
      setLastName(lname).
      setEmail(email).
      setEmailConfirmed(true).
    build()

  }
}
