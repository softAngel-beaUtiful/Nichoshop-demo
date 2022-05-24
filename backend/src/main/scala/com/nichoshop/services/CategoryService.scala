package com.nichoshop.services

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.model.dto._
import com.nichoshop.models
import com.nichoshop.models.helpers.DB
import com.nichoshop.utils.Conversions

class CategoryService {
  def subcategories(id: Int): List[models.CategoryEntity] = models.Categories.findByParentId(id)

  def sidebarCategories: List[models.CategoryEntity] = models.Categories.sidebarCategories.sortBy(_._1.ord).
    map {case (parent, children) => parent -> children.sortBy(_.ord)}.
    map {case (parent, children) => parent::children}.flatten


  def sidebarCategories2 = Conversions.toCategoryTree(models.Categories.sidebarCategories)

  def categoriesTree = Conversions.toCategoryTree(DB.read{ implicit s => models.Categories.query.list }, 0).get

  def categories(parentId: Int) =
    Conversions.toCategoryTree(DB.read{ implicit s => models.Categories.findById(parentId).toList ++ models.Categories.findByParentId(parentId)  }.map {
      case category if category.conditionType.isEmpty =>
        val cats = models.Categories.getCategoriesWithParents( List(category.id.get) ).map(c => c.id.get -> c).toMap

        def getConditionType(id: Int): Option[String] = {
          if (id == 0) None
          else {
            val next = cats(id)
            if (next.conditionType.isDefined) next.conditionType
            else getConditionType(next.parentId)
          }
        }
        category.copy(conditionType = getConditionType(category.id.get))
      case category => category
    }

      , parentId).get

  def createCategory(name: String, parentId: Int = 0) = DB.write  {implicit session =>
    models.Categories.insert(
      models.CategoryEntity(None, name, leaf = true, parentId, models.Categories.nextOrd(parentId))
    )
    models.Categories.flushCache()
  }

  def updateCategory(categoryId: Int, conditionType: Option[String] = None) = DB.write { implicit session =>
    val updated = models.Categories.query.filter(_.id === categoryId).map(_.conditionType).update(conditionType)
    models.Categories.flushCache()
    updated
  }

  def moveCategory(parentId: Int, childId: Int) = DB.write  {implicit session =>
    models.Categories.query.
      filter(_.id === childId).
      map(_.parentId).
      update(parentId)
  }

  def getAttributes(categoryId: Int) = DB.read { implicit session =>
    val categories = models.Categories.getCategoriesWithParents(List(categoryId)).flatMap(_.id)
    val attributes: List[models.ProductSpecificAttributeEntity]  = models.ProductSpecificAttributes.query.filter(_.catId inSet categories).list

    attributes.map {case models.ProductSpecificAttributeEntity(Some(id), catId, name, valueType, valueOptions, defaultValue, _, isMultivariation) =>
      ProductAttributeDto.newBuilder().
        setId(id).
        setCategoryId(catId).
        setName(name).
        setValueType(valueType match {
        case  models.ProductSpecificAttributes.ValueTypes.string => ProductAttributeType.STRING
        case  models.ProductSpecificAttributes.ValueTypes.integer => ProductAttributeType.INTEGER
        case  models.ProductSpecificAttributes.ValueTypes.unsignedInteger0 => ProductAttributeType.UNSIGNED_INTEGER0
        case  models.ProductSpecificAttributes.ValueTypes.unsignedInteger1 => ProductAttributeType.UNSIGNED_INTEGER1
        case  models.ProductSpecificAttributes.ValueTypes.enum => ProductAttributeType.ENUM
      }).setValueOptions(valueOptions.orNull).
        setDefaultValue(defaultValue.orNull).
        setIsMultivariation(isMultivariation).build

    }
  }

  def createAttribute(a: ProductAttributeDto) = DB.write {implicit session =>
    val attr = models.ProductSpecificAttributeEntity(
      catId = a.getCategoryId,
      name = a.getName,
      valueType = a.getValueType match {
        case ProductAttributeType.STRING => models.ProductSpecificAttributes.ValueTypes.string
        case ProductAttributeType.INTEGER => models.ProductSpecificAttributes.ValueTypes.integer
        case ProductAttributeType.UNSIGNED_INTEGER0 => models.ProductSpecificAttributes.ValueTypes.unsignedInteger0
        case ProductAttributeType.UNSIGNED_INTEGER1 => models.ProductSpecificAttributes.ValueTypes.unsignedInteger1
        case ProductAttributeType.ENUM => models.ProductSpecificAttributes.ValueTypes.enum
      },
      valueOptions = Option(a.getValueOptions),
      defaultValue = Option(a.getDefaultValue)
    )
    models.ProductSpecificAttributes.insert(attr)
  }

  def updateAttribute(id: Int, a: ProductAttributeDto) = DB.write {implicit session =>
    val attr = models.ProductSpecificAttributeEntity(
      id = Some(id),
      catId = a.getCategoryId,
      name = a.getName,
      valueType = a.getValueType match {
        case ProductAttributeType.STRING => models.ProductSpecificAttributes.ValueTypes.string
        case ProductAttributeType.INTEGER => models.ProductSpecificAttributes.ValueTypes.integer
        case ProductAttributeType.UNSIGNED_INTEGER0 => models.ProductSpecificAttributes.ValueTypes.unsignedInteger0
        case ProductAttributeType.UNSIGNED_INTEGER1 => models.ProductSpecificAttributes.ValueTypes.unsignedInteger1
        case ProductAttributeType.ENUM => models.ProductSpecificAttributes.ValueTypes.enum
      },
      valueOptions = Option(a.getValueOptions),
      defaultValue = Option(a.getDefaultValue)
    )
    models.ProductSpecificAttributes.update(attr)
  }

  def deleteAttribute(id: Int) = DB.write {implicit session =>
    models.ProductSpecificAttributes.delete(id)
  }
}
