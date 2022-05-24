package com.nichoshop.services

import com.nichoshop.legacy.dao.{MessageDAO, MessageFolderDAO}
import com.nichoshop.legacy.models.{MessageFoldersRow, MessagesRow}


class MessageService(messageDAO: MessageDAO, messageFolderDAO: MessageFolderDAO) {

  def findById(userId: Int, mid: Int): Option[MessagesRow] = messageDAO.findById(userId, mid)

  def deleteById(userId: Int, mid: Int) = messageDAO.deleteById(userId, mid)

  def allMessages(userId: Int, count: Int, page: Int) = messageDAO.getAllByUserId(userId, (page - 1) * count, count)

  def fromMembers(userId: Int, count: Int, page: Int) = messageDAO.fromMembers(userId, (page - 1) * count, count)

  def fromNichoshop(userId: Int, count: Int, page: Int) = messageDAO.fromNichoShop(userId, (page - 1) * count, count)

  def highPriority(userId: Int, count: Int, page: Int) = messageDAO.highPriority(userId, (page - 1) * count, count)

  def sent(userId: Int, count: Int, page: Int) = messageDAO.fromFolder(userId, -2, (page - 1) * count, count)

  def recycle(userId: Int, count: Int, page: Int) = messageDAO.fromFolder(userId, -1, (page - 1) * count, count)

  def fromFolder(userId: Int, fId: Int, count: Int, page: Int) = messageDAO.fromFolder(userId, fId, (page - 1) * count, count)

  def moveToFolder(userId: Int, ids: List[Int], fId: Int) = messageDAO.moveToFolder(userId, ids, fId)

  def addFolder(folder: MessageFoldersRow) = messageFolderDAO.create(folder)

  def deleteFolderById(userId: Int, fid: Int) = messageFolderDAO.deleteById(userId, fid)

  def getFoldersByUserId(userId: Int) = messageFolderDAO.getAllByUserId(userId)

  def markAsRead(userId: Int, ids: List[Int]) = messageDAO.markAsRead(userId, ids)

  def markAsUnread(userId: Int, ids: List[Int]) = messageDAO.markAsUnread(userId, ids)

  def markAsFlagged(userId: Int, ids: List[Int]) = messageDAO.markAsFlagged(userId, ids)

  def markAsUnflagged(userId: Int, ids: List[Int]) = messageDAO.markAsUnflagged(userId, ids)

  def searchByKeyword(userId: Int, keyword: String, searchSpace: Int, count: Int, page: Int) =
    messageDAO.searchByKeyword(userId, keyword, searchSpace, (page - 1) * count, count)

  def searchBySenderName(userId: Int, senderName: String, searchSpace: Int, count: Int, page: Int) =
    messageDAO.searchBySenderName(userId, senderName, searchSpace, (page - 1) * count, count)

  def searchByItemId(userId: Int, itemId: Int, searchSpace: Int, count: Int, page: Int) =
    messageDAO.searchByItemId(userId, itemId, searchSpace, (page - 1) * count, count)
}
