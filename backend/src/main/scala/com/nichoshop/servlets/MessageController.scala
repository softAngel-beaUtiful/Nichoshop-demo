package com.nichoshop.servlets

import com.nichoshop.legacy.models.MessageFoldersRow
import com.nichoshop.services.MessageService
import com.nichoshop.servlets.swagger.MessageOperations
import org.scalatra.swagger.Swagger

class MessageController(messageService: MessageService)(implicit val swagger: Swagger)
  extends AuthServlet with Json with MessageOperations {

//  before() {
//    if (!isAuthenticated)
//      scentry.authenticate("RememberMe")
//
//    requireLogin()
//  }

  get("/:id", operation(getById)) {
    okOrNotFound(messageService.findById(uid, params("id").toInt))
  }
  delete("/:id", operation(deleteById)) {
    messageService.deleteById(uid, params("id").toInt)
  }

  get("/p/:page/c/:count", operation(allMessages)) {
    messageService.allMessages(uid, params("count").toInt, params("page").toInt)
  }
  get("/from_members/p/:page/c/:count", operation(fromMembers)) {
    messageService.fromMembers(uid, params("count").toInt, params("page").toInt)
  }
  get("/from_shop/p/:page/c/:count", operation(fromNichoshop)) {
    messageService.fromNichoshop(uid, params("count").toInt, params("page").toInt)
  }
  get("/high_priority/p/:page/c/:count", operation(highPriority)) {
    messageService.highPriority(uid, params("count").toInt, params("page").toInt)
  }
  get("/sent/p/:page/c/:count", operation(sent)) {
    messageService.sent(uid, params("count").toInt, params("page").toInt)
  }
  get("/recycle/p/:page/c/:count", operation(recycle)) {
    messageService.recycle(uid, params("count").toInt, params("page").toInt)
  }
  get("/folder/:fid/p/:page/c/:count", operation(fromFolder)) {
    messageService.fromFolder(uid, params("fid").toInt, params("count").toInt, params("page").toInt)
  }
  put("/folder/:fid", operation(moveToFolder)) {
    messageService.moveToFolder(uid, parsedBody.extract[List[Int]], params("fid").toInt)
  }
  put("/read", operation(markAsRead)) {
    messageService.markAsRead(uid, parsedBody.extract[List[Int]])
  }
  put("/unread", operation(markAsUnread)) {
    messageService.markAsUnread(uid, parsedBody.extract[List[Int]])
  }
  put("/flagged", operation(markAsFlagged)) {
    messageService.markAsFlagged(uid, parsedBody.extract[List[Int]])
  }
  put("/unflagged", operation(markAsUnflagged)) {
    messageService.markAsUnflagged(uid, parsedBody.extract[List[Int]])
  }
  post("/folder/:name", operation(addFolder)) {
    messageService.addFolder(MessageFoldersRow(0, params("name"), uid))
  }
  delete("/folder/:fid", operation(deleteFolderById)) {
    messageService.deleteFolderById(uid, params("fid").toInt)
  }
  get("/folder", operation(getFoldersByUserId)) {
    messageService.getFoldersByUserId(uid)
  }
  get("/s/keyword/:keyword/space/:space/p/:page/c/:count", operation(searchByKeyword)) {
    messageService.searchByKeyword(uid, params("keyword"),
      params("space").toInt, params("count").toInt, params("page").toInt)
  }
  get("/s/sender/:sender/space/:space/p/:page/c/:count", operation(searchBySenderName)) {
    messageService.searchBySenderName(uid, params("sender"),
      params("space").toInt, params("count").toInt, params("page").toInt)
  }
  get("/s/item_id/:item_id/space/:space/p/:page/c/:count", operation(searchByItemId)) {
    messageService.searchByItemId(uid, params("item_id").toInt,
      params("space").toInt, params("count").toInt, params("page").toInt)
  }

}
