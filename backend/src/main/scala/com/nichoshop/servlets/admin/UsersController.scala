package com.nichoshop.servlets.admin

import com.nichoshop.marshalling.Marshallers
import com.nichoshop.model.dto.Role
import com.nichoshop.services.{PermissionsService, Services}
import com.nichoshop.servlets.Json
import com.nichoshop.servlets.admin.UsersController._
import com.nichoshop.servlets.customer.CustomerController
import org.json4s.JsonAST.JArray
import org.slf4j.LoggerFactory

/**
 * Created by Evgeny Zhoga on 29.11.15.
 */
class UsersController extends CustomerController with Json {
  private val log = LoggerFactory.getLogger(getClass)
  def name = "permissions"

  get("/") {
    try {
      Services.authService.withUser { user =>() =>
        val p = PermissionsService.Permissions.`users:get`
        Services.permissions.withProtection(p)(user) {
          val users = Services.usersService.getUserList()
          JArray(
            users.map(Marshallers.toJson)
          )
        }.getOrElse(halt(Marshallers.unauthorized(s"Permission required: ${p.name}")))
      }
    } catch {
      case e =>
        log.error("", e)
        throw e
    }
  }

  get("/:userId") {
    try {
      Services.authService.withUser { user =>() =>
        val p = PermissionsService.Permissions.`user:get`
        Services.permissions.withProtection(p)(user) {
          Services.usersService.getUser(params("userId").toInt).map(Marshallers.toJson).
            getOrElse(halt(Marshallers.notFound("User not found")))
        }.getOrElse(halt(Marshallers.unauthorized(s"Permission required: ${p.name}")))
      }
    } catch {
      case e =>
        log.error("", e)
        throw e
    }
  }

  post("/:userId/roles") {
    try {
      Services.authService.withUser { user =>() =>
        val p = PermissionsService.Permissions.`tpermission:add`

        Services.permissions.withProtection(p)(user) {
          val perm = parsedBody.extract[Permission]
          val p = Role.valueOf(perm.name)
          if (p == Role.CUSTOMER_SUPPORT || p == Role.ADMIN) {
            Marshallers.unauthorized(s"Adding 'CUSTOMER_SUPPORT' permission is not allowed")
          } else {
            Services.usersService.addPermissionToUser(params("userId").toInt, p)
            Marshallers.ok()
          }
        }.getOrElse(halt(Marshallers.unauthorized(s"Permission required: ${p.name}")))
      }
    } catch {
      case e: Throwable =>
        log.error("", e)
        throw e
    }
  }

  delete("/:userId/role/:permission") {
    try {
      Services.authService.withUser { user =>() =>
        val p = PermissionsService.Permissions.`tpermission:remove`

        Services.permissions.withProtection(p)(user) {
          val p = Role.valueOf(params("permission"))

          if (p == Role.CUSTOMER_SUPPORT) {
            Marshallers.unauthorized(s"Deleting 'CUSTOMER_SUPPORT' permission is not allowed")
          } else {
            Services.usersService.removePermissionFromUser(params("userId").toInt, p)
            Marshallers.ok()
          }
        }.getOrElse(halt(Marshallers.unauthorized(s"Permission required: ${p.name}")))
      }
    } catch {
      case e: Throwable =>
        log.error("", e)
        throw e
    }
  }

}

object UsersController {
  case class Permission(name: String)
}
