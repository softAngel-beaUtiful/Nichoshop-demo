package com.nichoshop.services

import com.nichoshop.model.dto._

import scala.collection.JavaConversions._

/**
 * Created by Evgeny Zhoga on 29.11.15.
 */
class PermissionsService {
  def withProtection[T](permission: Permission)(user: UserDto)(f: => T): Option[T] = {
    if (
      (permission.allowed(Role.CUSTOMER) && user.getRoles.contains(Role.CUSTOMER)) ||
        (permission.allowed(Role.ADMIN) && user.getRoles.contains(Role.ADMIN)) ||
        (permission.allowed(Role.CUSTOMER_SUPPORT) && user.getPermissions.toList.exists(_.getCode == permission.name))
    ) Some(f)
    else None
  }
}

sealed trait Permission {
  def name: String

  def description: String

  def allowed(role: Role): Boolean
}

object PermissionsService {
  val customers = List(Role.CUSTOMER)
  val customerSupport = List(Role.CUSTOMER_SUPPORT)
  val admin = List(Role.ADMIN)
  val all = customers ::: customerSupport ::: admin
  val admins = admin ::: customerSupport

  private def p(n: String, d: String, roles: List[Role]) = {
    val p = new Permission {
      val name = n
      val description = d

      def allowed(role: Role) = roles.contains(role)
    }
    Permissions._m += p.name -> p
    p
  }

  object Permissions {
    private[PermissionsService] val _m = scala.collection.mutable.HashMap.empty[String, Permission]

    val `tpermission:add` = p("tpermission-add", "Protect 'Add permission type' handler", admin)
    val `tpermission:remove` = p("tpermission-remove", "Protect 'Remove permission type' handler", admin)
    val `permission:add` = p("permission-add", "Protect 'Add permission' handler", admin)
    val `permission:remove` = p("permission-remove", "Protect 'Remove permission' handler", admin)
    val `users:get` = p("users-get", "Protect 'Get users list' handler", admin)
    val `user:get` = p("user-get", "Protect 'Get user' handler", admin)

    val `customerSupports:get` = p("customer_supports-get", "Protect 'Get customer support list' handler", admin)
    val `customerSupport:get` = p("customer_support-get", "Protect 'Get customer support' handler", admin)
    val `customerSupport:add` = p("customer_support-add", "Protect 'Add customer support' handler", admin)

    val `categories:add` = p("categories-add", "Protect 'Add category' handler", customerSupport)
    val `category:move` = p("category-move", "Protect 'Move category to another parent' handler", customerSupport)

    val map = _m.toMap
  }
}
