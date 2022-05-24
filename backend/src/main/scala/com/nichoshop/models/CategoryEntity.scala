package com.nichoshop.models

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.models.helpers._

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.Future
import scala.slick.lifted.Tag

/**
 * Created by Evgeny Zhoga on 27.06.15.
 */
case class CategoryEntity
(
  override val id: Option[Int],
  name: String,
  leaf: Boolean,
  parentId: Int = 0,
  ord: Int = 0,
  conditionType: Option[String] = None
  ) extends IdAsPrimaryKey

class Categories(tag:Tag) extends TableWithIdAsPrimaryKey[CategoryEntity](tag, "categories") {
  def name = column[String]("name")
  def leaf = column[Boolean]("leaf")
  def parentId = column[Int]("parent_id")
  def ord = column[Int]("ord")
  def conditionType = column[Option[String]]("condition_type")

  def * = ( id.?, name, leaf, parentId, ord, conditionType) <> ( CategoryEntity.tupled, CategoryEntity.unapply )
}

object Categories extends QueryForTableWithIdAsPrimaryKey[CategoryEntity, Categories](TableQuery[Categories]) {
  private case class CategoriesCache(timestamp: Long, idWithParentId: List[Tuple2[Int, Int]]) {
    val asMap = idWithParentId.toMap
  }

  private val categoriesCache = new AtomicReference[CategoriesCache]

  def findByParentId(parentId: Int): List[CategoryEntity] = DB.read { implicit session =>
    query.filter(_.parentId === parentId).list
  }
  private def getCategoriesCache = DB.read { implicit session =>
    Option(categoriesCache.get()).
      fold {
      val cc = CategoriesCache(System.currentTimeMillis(), query.map(c => c.id -> c.parentId).list)
      categoriesCache.set(cc)
      cc
    } {cc =>
      if (cc.timestamp + TimeUnit.HOURS.toMillis(3) < System.currentTimeMillis()) {
        val cc = CategoriesCache(System.currentTimeMillis(), query.map(c => c.id -> c.parentId).list)
        categoriesCache.set(cc)
        cc
      } else {
        cc
      }
    }
  }

  def flushCache(): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global

    categoriesCache.set(null)

    Future {
      getCategoriesCache
    }
  }

  def getCategoriesWithParents(categoryIds: List[Int]) = {
    val cc = getCategoriesCache.asMap
    val categories = categoryIds.flatMap {catId =>
      def tail(parentId: Int): List[Int] = {
        if (parentId == 0) Nil
        else parentId :: tail(cc(parentId))
      }
      tail(catId)
    }.toSet
    DB.read { implicit session =>
      query.filter(c => c.id inSet categories).list
    }
  }

  def getCategoriesWithParentsOrdered(categoryIds: List[Int]) = {
    val cc: Map[Int, com.nichoshop.models.CategoryEntity] = getCategoriesWithParents(categoryIds).map(c => c.id.get -> c).toMap

    def tail(parent: Option[com.nichoshop.models.CategoryEntity]): List[com.nichoshop.models.CategoryEntity] = {
      if (parent.isEmpty) Nil
      else parent.get :: tail(cc.get(parent.get.parentId))
    }

    categoryIds.map { id =>
      id -> tail(cc.get(id))
    }.toMap
  }

  def sidebarCategories: List[(CategoryEntity, List[CategoryEntity])] = DB.read { implicit session =>
    val firstLevel = findByParentId(0)

    val secondLevel = (for {
      category <- Categories.query
      if category.parentId inSet firstLevel.map(_.id.get).toSet
    } yield category).list.groupBy(_.parentId)

    firstLevel.map(c1 => c1 -> secondLevel.getOrElse(c1.id.get, List.empty))
  }

  def nextOrd(parentId: Int = 0) = DB.read { implicit session =>
    query.filter( _.parentId === parentId ).map(_.ord).run.headOption.map(_ + 1).getOrElse(1)
  }

  sealed trait Condition {
    def name: String
    def code: String
    def values: List[String]
  }
  private def c(id: String, n: String, vs: String*) = new Condition {
    val values: List[String] = vs.toList
    val name: String = n
    val code: String = id
  }
  object Conditions {
    val c1 = c("condition1", "Condition Type 1", "New")
    val c2 = c("condition2", "Condition Type 2", "New", "Used")
    val c3 = c("condition3", "Condition Type 3", "New with tags", "New without tags", "New with defects", "Pre-owned")
    val c4 = c("condition4", "Condition Type 4", "Brand New", "Like New", "Very Good", "Good", "Accessible")
    val c5 = c("condition5", "Condition Type 5", "New", "New other (see details)", "Used", "For parts or not working")
    val c6 = c("condition6", "Condition Type 6", "New", "Certified pre-owned", "Used")
    val c7 = c("condition7", "Condition Type 7", "New", "New other (see details)", "Remanufactured", "Used", "For parts or not working")
    val c8 = c("condition8", "Condition Type 8", "New", "New other (see details)", "Manufacturer refurbished", "Seller refurbished", "Used", "For parts or not working")
  }
}