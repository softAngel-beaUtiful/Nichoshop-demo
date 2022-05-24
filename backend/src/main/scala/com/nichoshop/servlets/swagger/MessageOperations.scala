package com.nichoshop.servlets.swagger

import com.nichoshop.legacy.models.{Manifests, MessageFoldersRow, MessagesRow}

trait MessageOperations extends ApiDescription[MessagesRow] {

  def name = "Message"

  implicit def manifestForT: Manifest[MessagesRow] = Manifests.message


  val allMessages = (apiOperation[List[MessagesRow]]("allMessages")
    summary s"Returns all user's inbox"
    tags "Message"
    parameters(pathParam[Int]("page").description("page number"),
    pathParam[Int]("count").description("messages per page"))
    )

  val fromMembers = (apiOperation[List[MessagesRow]]("fromMembers")
    summary s"Returns user's mail from other users"
    tags "Message"
    parameters(pathParam[Int]("page").description("page number"),
    pathParam[Int]("count").description("messages per page"))
    )

  val fromNichoshop = (apiOperation[List[MessagesRow]]("fromNichoshop")
    summary s"Returns user's mail from Nichoshop"
    tags "Message"
    parameters(pathParam[Int]("page").description("page number"),
    pathParam[Int]("count").description("messages per page"))
    )

  val highPriority = (apiOperation[List[MessagesRow]]("highPriority")
    summary s"Returns user's high priority mail"
    tags "Message"
    parameters(pathParam[Int]("page").description("page number"),
    pathParam[Int]("count").description("messages per page"))
    )

  val sent = (apiOperation[List[MessagesRow]]("sent")
    summary s"Returns user's sent mail"
    tags "Message"
    parameters(pathParam[Int]("page").description("page number"),
    pathParam[Int]("count").description("messages per page"))
    )

  val recycle = (apiOperation[List[MessagesRow]]("recycle")
    summary s"Returns user's mail from recycle bin"
    tags "Message"
    parameters(pathParam[Int]("page").description("page number"),
    pathParam[Int]("count").description("messages per page"))
    )

  val fromFolder = (apiOperation[List[MessagesRow]]("fromFolder")
    summary s"Returns user's mail from particular folder"
    tags "Message"
    parameters(pathParam[Int]("fid").description("folder id"),
    pathParam[Int]("page").description("page number"),
    pathParam[Int]("count").description("messages per page"))
    )

  val moveToFolder = (apiOperation[Unit]("moveToFolder")
    summary s"Moves marked user's messages to particular folder"
    tags "Message"
    parameter bodyParam[List[Int]].description("Array of marked message ids")
    )

  val markAsRead = (apiOperation[Unit]("markAsRead")
    summary s"Marks user's messages as read"
    tags "Message"
    parameters bodyParam[List[Int]].description("Array of marked message ids")
    )

  val markAsUnread = (apiOperation[Unit]("markAsUnread")
    summary s"Marks user's messages as unread"
    tags "Message"
    parameter bodyParam[List[Int]].description("Array of marked message ids")
    )

  val markAsFlagged = (apiOperation[Unit]("markAsFlagged")
    summary s"Marks user's messages as flagged"
    tags "Message"
    parameter bodyParam[List[Int]].description("Array of marked message ids")
    )

  val markAsUnflagged = (apiOperation[Unit]("markAsUnflagged")
    summary s"Marks user's messages as unflagged"
    tags "Message"
    parameter bodyParam[List[Int]].description("Array of marked message ids")
    )

  val addFolder = (apiOperation[Unit]("addFolder")
    summary s"Adds new folder to user's inbox"
    tags "Message"
    parameter pathParam[String]("name").description("Folder name")
    )

  val deleteFolderById = (apiOperation[Unit]("deleteFolderById")
    summary s"Deletes folder by id"
    tags "Message"
    parameter pathParam[Int]("id").description("folder id")
    )

  val getFoldersByUserId = (apiOperation[List[MessageFoldersRow]]("getFoldersByUserId")
    summary s"Returns user's mailbox folders"
    tags "Message"
    parameter pathParam[Int]("id").description("user id")
    )

  val searchByKeyword = (apiOperation[List[MessagesRow]]("searchByKeyword")
    summary s"Finds messages by searching for keyword in message's subject or item's title"
    tags "Message"
    notes "space can take values: 0 - all messages, -1 - recycle, -2 - sent, -3 - from members, -4 - from Nichoshop, -5 - high priority, i - folder #i"
    parameters(pathParam[String]("keyword").description("search string"),
    pathParam[Int]("space").description("search space"),
    pathParam[Int]("page").description("page number"),
    pathParam[Int]("count").description("messages per page"))
    )

  val searchBySenderName = (apiOperation[List[MessagesRow]]("searchBySenderName")
    summary s"Finds messages by sender's name"
    tags "Message"
    notes "space can take values: 0 - all messages, -1 - recycle, -2 - sent, -3 - from members, -4 - from Nichoshop, -5 - high priority, i - folder #i"
    parameters(pathParam[String]("sender").description("search string"),
    pathParam[Int]("space").description("search space"),
    pathParam[Int]("page").description("page number"),
    pathParam[Int]("count").description("messages per page"))
    )

  val searchByItemId = (apiOperation[List[MessagesRow]]("searchByItemId")
    summary s"Finds messages by item's id"
    tags "Message"
    notes "space can take values: 0 - all messages, -1 - recycle, -2 - sent, -3 - from members, -4 - from Nichoshop, -5 - high priority, i - folder #i"
    parameters(pathParam[Int]("item_id").description("search string"),
    pathParam[Int]("space").description("search space"),
    pathParam[Int]("page").description("page number"),
    pathParam[Int]("count").description("messages per page"))
    )

}
