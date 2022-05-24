package com.nichoshop.servlets

import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.{NotFound, Ok}

import javax.servlet.http.HttpServletRequest

trait Json extends JacksonJsonSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats + AccountTypeSerializer

  def name: String

  before() {
    contentType = formats("json")
  }

  case class JError(error: String)

  def okOrNotFound[E](op: Option[Any]) = op match {
    case Some(x) => Ok(x)
    case None => NotFound(JError(s"$name Not Found"))
  }

  implicit class List2Paging[T](l: List[T]) {
    def paged(count: Int, page: Int) = {
      val pages = l.grouped(count).toList
      if (page - 1 >= pages.size) Nil
      else pages(page - 1)
    }

    def paged(implicit request: HttpServletRequest): List[T] = paged(params("count").toInt, params("page").toInt)
  }

  implicit class Boolean2Digit(b: Boolean) {
    def toDigit = if (b) 1 else 0
  }

//  protected override def transformResponseBody(body: JValue): JValue = body.underscoreKeys

//  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys
}
