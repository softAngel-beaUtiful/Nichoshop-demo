package com.nichoshop.models

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.models.helpers._

import scala.slick.lifted.Tag

/**
 * Created by Evgeny Zhoga on 03.07.15.
 */
case class PermissionEntity
(
  override val id: Option[Int],
  userId: Int,
  code: String
  ) extends IdAsPrimaryKey

class Permissions (tag:Tag) extends TableWithIdAsPrimaryKey[PermissionEntity](tag, "permissions") {
  def userId = column[Int]("user_id")
  def code = column[String]("code")

  def * = ( id.?, userId, code) <> ( PermissionEntity.tupled, PermissionEntity.unapply )

}

case class TPermission
(
  override val id: Option[Int],
  userId: Int,
  permissionType: String
  ) extends IdAsPrimaryKey

class TPermissions (tag:Tag) extends TableWithIdAsPrimaryKey[TPermission](tag, "tpermissions") {
  def userId = column[Int]("user_id")
  def permissionType = column[String]("type")

  def * = ( id.?, userId, permissionType) <> ( TPermission.tupled, TPermission.unapply )

}

object Permissions extends QueryForTableWithIdAsPrimaryKey[PermissionEntity, Permissions](TableQuery[Permissions]) {

  object buying {
    val name = "Buying"
    object started {
      val name = "Getting Started"
    }
  }
    val buyingProblems = "Resolving buying problems"
    val selling = ""
}

object TPermissions extends QueryForTableWithIdAsPrimaryKey[TPermission, TPermissions](TableQuery[TPermissions]) {
  object Type {
    val customer = "customer"
    val customerSupport = "customerSupport"
    val admin = "admin"
  }
}