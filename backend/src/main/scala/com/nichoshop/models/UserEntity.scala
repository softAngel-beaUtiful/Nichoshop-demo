package com.nichoshop.models

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.model.AccountType
import com.nichoshop.models.helpers._
import org.mindrot.jbcrypt.BCrypt

import scala.slick.lifted.{Index, Tag}

/**
 * Created by Evgeny Zhoga on 13.06.15.
 */
case class UserEntity(
                       override val id: Option[Int] = None,
                       userid: String,
                       password: String,
                       email: String,
                       name: String,
                       lname: String,
                       registrationDate: Long = System.currentTimeMillis(),
                       emailConfirmed: Boolean = false,
                       suspended: Boolean = false,
                       deleted: Boolean = false,
                       deletedDate: Option[Long] = None,
                       giftCardBalance: Int = 0,
                       question: Option[String] = None,
                       answer: Option[String] = None,
                       registrationAddressId: Option[Int] = None,
                       fromAddressId: Option[Int] = None,
                       toAddressId: Option[Int] = None,
                       returnAddressId: Option[Int] = None,
                       paymentAddressId: Option[Int] = None,
                       phone: Option[String] = None,
                       accountType: AccountType
                     ) extends IdAsPrimaryKey

class Users(tag: Tag) extends TableWithIdAsPrimaryKey[UserEntity](tag, "users") {
  implicit val accountTypeMapper = MappedColumnType.base[AccountType, String](_.toString, AccountType.valueOf)

  def userid = column[String]("userid")

  def password = column[String]("password")

  def email = column[String]("email")

  def name = column[String]("name")

  def lname = column[String]("lname")

  def registrationDate = column[Long]("registration_date")

  def emailConfirmed = column[Boolean]("email_confirmed")

  def suspended = column[Boolean]("suspended")

  def deleted = column[Boolean]("deleted")

  def deletedDate = column[Option[Long]]("deleted_date")

  def giftCardBalance = column[Int]("gift_card_balance")

  def question = column[Option[String]]("question")

  def answer = column[Option[String]]("answer")

  def registrationAddressId = column[Option[Int]]("registration_address_id")

  def fromAddressId = column[Option[Int]]("from_address_id")

  def toAddressId = column[Option[Int]]("to_address_id")

  def returnAddressId = column[Option[Int]]("return_address_id")

  def paymentAddressId = column[Option[Int]]("payment_address_id")

  def phone = column[Option[String]]("phone")

  def accountType: Column[AccountType] = column[AccountType]("account_type")

  def uniqueEmailIndex: Index = index("unique_email", email, unique = true)

  def uniqueUseridIndex: Index = index("unique_userid", userid, unique = true)

  def * = (id.?, userid, password, email, name, lname, registrationDate,
    emailConfirmed, suspended, deleted, deletedDate, giftCardBalance, question,
    answer, registrationAddressId, fromAddressId, toAddressId, returnAddressId, paymentAddressId,
    phone, accountType) <> (UserEntity.tupled, UserEntity.unapply)
}

object Users extends QueryForTableWithIdAsPrimaryKey[UserEntity, Users](TableQuery[Users]) {
  def findByEmailOrUserid(emailOrUserid: String): Option[UserEntity] = DB.read({ implicit s =>
    query.filter(u => u.email === emailOrUserid || u.userid === emailOrUserid).firstOption
  })

  def findByEmail(email: String): Option[UserEntity] = DB.read({ implicit s =>
    query.filter(_.email === email).firstOption
  })

  def findByUserid(userid: String): Option[UserEntity] = DB.read({ implicit s =>
    query.filter(_.userid === userid).firstOption
  })

  def findByPhone(phone: String): Option[UserEntity] = DB.read({ implicit s =>
    query.filter(_.phone === phone).firstOption
  })

  def encryptPassword(plain: String): String = BCrypt.hashpw(plain, BCrypt.gensalt())
}
