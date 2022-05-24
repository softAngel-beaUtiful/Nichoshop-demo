package com.nichoshop

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.legacy.dao.slick._
import com.nichoshop.legacy.models._
import com.nichoshop.utils.db.DataSource

import java.util.Properties
import scala.util.Random

object Utils {
  def test(dataSource: javax.sql.DataSource): Unit = {

    val c = dataSource.getConnection
    val stmt = c.createStatement()
    val rs = stmt.executeQuery("select * from users")
    println(new Iterator[String] {
      def hasNext = rs.next()

      def next() = rs.getString("name")
    }.toList)
    rs.close()
    stmt.close()
    c.close()
  }

  val dailyDealsId = 21324

  def createUser(id: Int) = {
    val uid = "user" + id
    UsersRow(id, uid, "pass" + uid, uid + "@gmail.com", uid + "name", uid + "lname", System.currentTimeMillis, true, false, false, phone = None)
  }

  def createCategory(id: Int) = {
    CategoriesRow(id, s"category$id", true, 1, id)
  }

  def createProduct(id: Int) = {
    ProductsRow(id, id % 4, dailyDealsId, s"product title $id", "subtitle", 5, "images/path", fixedPrice = true, 10000, None,
      5, "USA", System.currentTimeMillis, 0, ProductDetailsRow(s"description $id"))
  }

  def createMessage(toId: Int) = {
    MessagesRow(0, Some(Random.nextInt(5) + 1), None, toId, "subject", "message", System.currentTimeMillis(), false, false)
  }

  def userWithoutId(user: UsersRow) = {
    val l = user.productIterator.toList.tail
    (l(0), l(1), l(2), l(3), l(4), l(5), l(6), l(7), l(8), l(9), l(10), l(11), l(12), l(13), l(14), l(15), l(16), l(17))
  }
}

object Test extends App {

  val props = new Properties
  props.load(Thread.currentThread.getContextClassLoader.getResourceAsStream("nichoshop.properties"))
  DataSource.create(props)
  val db = Database.forDataSource(DataSource.dataSource)
  //  db.withSession{ implicit session =>
  //    Users.list.foreach(println)
  //    val u = Users.filter(_.userid==="easda").list
  //    println(u)
  //  }
  //  val us = UsersRow(3242, "sliiiii", "pass", "asdfas@gmail.com", "name", "lname", System.currentTimeMillis(), true, false, false)
  val sud = new SlickUserDAO(db)
  val spd = new SlickProductDAO(db)
  val scd = new SlickCategoryDAO(db)
  val sPurchDao = new SlickPurchaseDAO(db)
  val smd = new SlickMessageDAO(db)

  //    scd.create(createCategory(1))
  //    scd.create(createCategory(2))
  //    scd.create(createCategory(3))
  //    scd.create(createCategory(4))
  //
  //    sud.create(createUser(30))
  //    sud.create(createUser(65))
  //    sud.create(createUser(66))
  //    sud.create(createUser(67))
  //  UserService.updateById(9,us.copy(email="femail@mail.ru"))
  //  UserService.findById(9).foreach(println)
  //case class UsersRow(id: Int, userid: String, password: String, email: String, name: String, lname: String, registrationDate: Long, emailConfirmed: Boolean, suspended: Boolean, deleted: Boolean, deletedDate: Option[Long] = None, giftCardBalance: Int = 0, question: Option[String] = None, answer: Option[String] = None, registrationAddressId: Option[Int] = None, fromAddressId: Option[Int] = None, toAddressId: Option[Int] = None, returnAddressId: Option[Int] = None, paymentAddressId: Option[Int] = None)

  //  createUser(1).productIterator.toList
  //  sud.findAll foreach println
  //  println(userWithoutId(createUser(1)))
  //    println(sud.findById(1))
  //    println(sud.fromToken("ddafkjasdfksjsadfa"))

  //  spd.create(createProduct(30))
  //  spd.create(createProduct(65))
  //  spd.create(createProduct(66))
  //  spd.create(createProduct(67))

  //  println(spd.findAll.map(_.productDetails))
  //  println(spd.randomWithCategory(10, 21324))
  //  println(spd.test)

  //  smd.create(createMessage(2))
  //  smd.create(createMessage(2))
  println(spd.filterByState(2, 1, 4, 0, 10))
}

