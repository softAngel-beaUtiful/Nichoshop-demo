package com.nichoshop.utils

import com.nichoshop.legacy.models.{SessionsRow, UsersRow}
import com.nichoshop.model.dto._
import com.nichoshop.services.PermissionsService

import java.util.concurrent.TimeUnit

//.{Token => PublicToken, User => PublicUser, Session => PublicSession, Category => PublicCategory, Product => PublicProduct, ProductVariant => PublicProductVariant, TPermissions, CategoryTree}
import com.nichoshop.models
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

/**
 * Methods to convert DB representation to serializable
 *
 * Created by Evgeny Zhoga on 03.06.15.
 */
object Conversions {
  private val log = LoggerFactory.getLogger(getClass)
  @deprecated(message = "Use com.nichoshop.models.Session class for DB representation")
  def toSession(session: SessionsRow): SessionDto = toSession(session.userId, session.hash, session.creationTime)

  def toSession(session: models.SessionEntity): SessionDto = toSession(session.userId, session.hash, session.creationTime)

  def toSession(userId: String,
                 sessionId: String,
                  timestamp: Long): SessionDto = SessionDto.
    newBuilder().
    setId(sessionId).
    setUserid(userId).
    setTimestamp(timestamp).
    build

  def toToken(token: models.TokenEntity): TokenDto = TokenDto.newBuilder().
    setHash(token.hash).
    setHashSession(token.hashSession).
    setUserid(token.userid).
    setTimestamp(token.creationTime).
    setCreated(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(365)).
    build()


  @deprecated(message = "Use com.nichoshop.models.User class for DB representation")
  def toUser(u: UsersRow) =
    UserDto.newBuilder().
      setId(u.id).
      setUserid(u.userid).
      setEmail(u.email).
      setEmailConfirmed(u.emailConfirmed).
      setFirstName(u.name).
      setLastName(u.lname).
      setRegistrationTimestamp(u.registrationDate).
      setSuspended(u.suspended).
      build()

  def toUser(u: models.UserEntity, tpermissions: List[models.TPermission], permissions: List[models.PermissionEntity]) = {
    val p = permissions.filter(p => PermissionsService.Permissions.map.contains(p.code)).map(toPermission)
    UserDto.newBuilder().
      setId(u.id.get).
      setUserid(u.userid).
      setEmail(u.email).
      setEmailConfirmed(u.emailConfirmed).
      setFirstName(u.name).
      setLastName(u.lname).
      setRegistrationTimestamp(u.registrationDate).
      setSuspended(u.suspended).
      setRoles(tpermissions.flatMap {
      case models.TPermission(_, _, models.TPermissions.Type.admin) => Some(Role.ADMIN)
      case models.TPermission(_, _, models.TPermissions.Type.`customerSupport`) => Some(Role.CUSTOMER_SUPPORT)
      case models.TPermission(_, _, models.TPermissions.Type.customer) => Some(Role.CUSTOMER)
    }).
      setPermissions(p).
      build()
  }

  def toPermission(permission: models.PermissionEntity) = Permission.newBuilder().
    setId(permission.id.get).
    setUserid(permission.userId).
    setCode(permission.code).build()

  val ROOT_CATEGORY = CategoryDto.newBuilder().setId(0).setParentId(0).setName("ROOT").build()

  def toCategory(category: models.CategoryEntity) = CategoryDto.newBuilder().
    setId(category.id.get).
    setName(category.name).
    setParentId(category.parentId).
    setConditionType(category.conditionType.orNull).
    build()

  def toCategoryTree(categories: List[(models.CategoryEntity, List[models.CategoryEntity])]) = {
    CategoryTreeDto.newBuilder().
      setCategory(ROOT_CATEGORY).
      setChildren(
        categories.map {case (parent, children) =>
          CategoryTreeDto.newBuilder().
            setCategory(toCategory(parent)).
            setChildren(
              children.map(c => CategoryTreeDto.newBuilder().setCategory(toCategory(c)).setChildren(List()).build())
            ).build()
        }
      ).build()
  }
  def toCategoryTree(categories: List[models.CategoryEntity], rootId: Int, level: Int = 2) = {
    val rootCategory =
      if (rootId == 0) Some(ROOT_CATEGORY)
      else categories.find(_.id.get == rootId).map(toCategory)

    rootCategory.map {root =>
      val parentToList = categories./*filter(_.id == root.getId).*/map(toCategory).groupBy(_.getParentId)

      def next(root: CategoryDto, level: Int): CategoryTreeDto = {
        val builder = CategoryTreeDto.newBuilder().
          setCategory(root)
        parentToList.get(root.getId) match {
          case Some(children) if level > 0=>
            builder.setChildren(children.map(c => next(c, level - 1)))
          case _ =>
            builder.setChildren(List())
        }
        builder.build()
      }

      next(root, level)
    }
  }

}
