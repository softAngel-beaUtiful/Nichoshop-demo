package com.nichoshop.services

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.model.AccountType
import com.nichoshop.model.dto._
import com.nichoshop.models
import com.nichoshop.models.helpers.DB
import com.nichoshop.services.converters._
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.LoggerFactory

/**
 * Created by Evgeny Zhoga on 22.11.15.
 */
class UsersService {
  val log = LoggerFactory.getLogger(getClass)

  def getUserAddresses(userId: Int): List[AddressDto] = DB.read { implicit session =>
    models.Addresses.query.filter(_.userId === userId).list.map(v => v: AddressDto)
  }

  def addPermissionToUser(userId: Int, role: Role) = {
    DB.write { implicit session =>
      models.TPermissions.insert(
        models.TPermission(id = None, userId = userId, getPermissionType(role))
      )
    }
  }

  def removePermissionFromUser(userId: Int, permission: Role) = {
    DB.write { implicit session =>
      models.TPermissions.query.filter(
        p => (p.permissionType === getPermissionType(permission)) && (p.userId === userId)
      ).delete
    }
  }

  def hasAdminOrCustomerSupportRole(user: UserDto): Boolean = {
    hasAdminRole(user) || hasCustomerSupportRole(user)
  }

  def hasCustomerSupportRole(user: UserDto): Boolean = {
    hasRole(user, Role.CUSTOMER_SUPPORT)
  }

  def hasAdminRole(user: UserDto): Boolean = {
    hasRole(user, Role.ADMIN)
  }

  private def hasRole(user: UserDto, tPermission: Role): Boolean = {
    user.getRoles.contains(tPermission)
  }

  private def isCustomerSupport(userId: Int) = getUser(userId).exists(_.getRoles.contains(Role.CUSTOMER_SUPPORT))

  def addPermissionToUser(userId: Int, permissionCode: String) = {
    DB.write { implicit session =>

      if (isCustomerSupport(userId)) {
        PermissionsService.Permissions.map.get(permissionCode).foreach { p =>
          models.Permissions.insert(
            models.PermissionEntity(id = None, userId = userId, p.name)
          )
        }
        true
      } else {
        false
      }
    }
  }

  def removePermissionFromUser(userId: Int, permissionCode: String) = {
    DB.write { implicit session =>
      PermissionsService.Permissions.map.get(permissionCode).foreach { p =>
        models.Permissions.query.filter(
          p1 => (p1.code === p.name) && (p1.userId === userId)
        ).delete
      }
    }
  }

  // TODO: vnavozenko: we need to refactor this method(create service for admin usage)
  def addUser(user: UserDto, nonEncryptedPassword: String, permissions: List[String]) = {
    DB.write { implicit session =>
      val hashed = BCrypt.hashpw(nonEncryptedPassword, BCrypt.gensalt())

      val u: models.UserEntity = models.UserEntity(
        userid = user.getUserid,
        password = hashed,
        email = user.getEmail,
        emailConfirmed = user.getEmailConfirmed,
        lname = user.getLastName,
        name = user.getFirstName,
        accountType = AccountType.SYSTEM
      )

      val id = models.Users.insert(u)
      //      val createdUser = u.copy(id = Some(id))

      addPermissionToUser(id, Role.CUSTOMER_SUPPORT)
      permissions.foreach { permission =>
        addPermissionToUser(id, permission)
      }
    }
  }

  def getUser(id: Int): Option[UserDto] = {
    DB.read { implicit session =>
      models.Users.findById(id).map { user =>
        val tpermissions = models.TPermissions.query.filter(_.userId === id).list
        val permissions = models.Permissions.query.filter(_.userId === id).list

        (user, tpermissions, permissions): UserDto
      }
    }
  }

  def getUserList(filters: UsersService.Filters = UsersService.Filters()): List[UserDto] = {
    DB.read { implicit session =>

      val users: List[models.UserEntity] = Option(models.Users.query).
        //map(). // filters will be here
        get.list

      val tpermissions: Map[Int, List[models.TPermission]] = models.TPermissions.query.
        filter(_.userId inSet users.map(_.id.get)).
        list.groupBy(_.userId)

      val permissions: Map[Int, List[models.PermissionEntity]] = models.Permissions.query.
        filter(_.userId inSet users.map(_.id.get)).
        list.groupBy(_.userId)

      import converters._

      users.map(u => (u, tpermissions.getOrElse(u.id.get, List.empty[models.TPermission]), permissions.getOrElse(u.id.get, List.empty[models.PermissionEntity])): UserDto)

    }
  }

  def getCustomerSupports(filters: UsersService.Filters = UsersService.Filters()): List[UserDto] = {
    DB.read { implicit session =>

      val users: List[models.UserEntity] = Option(for {
        user <- models.Users.query
        tpermissions <- models.TPermissions.query
        if (user.id === tpermissions.userId) && (tpermissions.permissionType === Role.CUSTOMER_SUPPORT.name())
      } yield user).
        //map(). // filters will be here
        get.list

      val tpermissions: Map[Int, List[models.TPermission]] = models.TPermissions.query.
        filter(_.userId inSet users.map(_.id.get)).
        list.groupBy(_.userId)

      val permissions: Map[Int, List[models.PermissionEntity]] = models.Permissions.query.
        filter(_.userId inSet users.map(_.id.get)).
        list.groupBy(_.userId)

      import converters._

      users.map(u => (u, tpermissions.getOrElse(u.id.get, List.empty[models.TPermission]), permissions.getOrElse(u.id.get, List.empty[models.PermissionEntity])): UserDto)
    }
  }

  private def getPermissionType(role: Role): String = {
    role match {
      case Role.ADMIN => models.TPermissions.Type.admin
      case Role.CUSTOMER_SUPPORT => models.TPermissions.Type.customerSupport
      case Role.CUSTOMER => models.TPermissions.Type.customer
    }
  }

  def findByEmailOrUserid(emailOrUserid: String) = models.Users.findByEmailOrUserid(emailOrUserid)
}

object UsersService {
  case class Filters(
                      permissionTypes: Option[List[Role]] = None
                    )
}
