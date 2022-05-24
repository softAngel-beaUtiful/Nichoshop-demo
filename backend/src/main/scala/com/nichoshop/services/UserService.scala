package com.nichoshop.services

import com.nichoshop.legacy.dao.UserDAO
import com.nichoshop.legacy.models.{UsersRow, EmailConfirmationRow}
import com.nichoshop.models.helpers.DB
import com.nichoshop.models.UserEntity
import org.mindrot.jbcrypt.BCrypt
import com.nichoshop.utils.auth.SecureUtils

class UserService(val userDAO: UserDAO) extends CommonService[UsersRow] {
  def confirmEmail(code: String): Boolean = {
    userDAO.confirmEmail(code)
  }

  def saveEmailConfirmationCode(emailConfirmationRow: EmailConfirmationRow) = {
    userDAO.saveEmailConfirmationCode(emailConfirmationRow)
  }

  def addPhoneAndSendSMS(userId: String, phone: String, code: String): Boolean = {
    
    if (checkPhoneAlreadyExist(phone)) {
      throw new RuntimeException(s"User with phone ${phone} already exist")
      false
    }
    
    findByUserId(userId).map(createdUser => {
      updateByUserId(
        createdUser.copy(phone = Some(phone))
      )
    })

    true
  }


  def dao = userDAO

  override def create(user: UsersRow) = {
    val hashed = BCrypt.hashpw(user.password, BCrypt.gensalt())
    dao.create(user.copy(password = hashed))
  }

  def checkUseridAlreadyExists(userid: String): Boolean = {
    userDAO.findByUserId(userid).isDefined
  }

  def checkEmailAlreadyExists(email: String): Boolean = {
    userDAO.findByEmail(email).isDefined
  }

  def checkPhoneAlreadyExist(phone: String): Boolean = {
    userDAO.findByPhone(phone).isDefined
  }

  def findByUserId(userid: String): Option[UsersRow] = userDAO.findByUserId(userid)

  def findByEmail(email: String): Option[UsersRow] = userDAO.findByEmail(email)

  def updateByUserId(x: UsersRow) = userDAO.updateByUserId(x)

  def deleteByUserId(userid: String) = userDAO.deleteByUserId(userid)
}
