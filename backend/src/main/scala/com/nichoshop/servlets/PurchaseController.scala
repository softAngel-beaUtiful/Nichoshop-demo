package com.nichoshop.servlets

import com.nichoshop.legacy.models.{Manifests, Purchase}
import com.nichoshop.marshalling.Marshallers
import com.nichoshop.services.Services
import com.nichoshop.servlets.swagger.ApiDescription
import org.slf4j.LoggerFactory

class PurchaseController//(purchaseService: PurchaseService)
                        //(implicit val swagger: Swagger)
  extends customer.CustomerController//AuthServlet with PurchaseOperations
  with Json {

  private val log = LoggerFactory.getLogger(getClass)
  def name = "customer/purchase"

  post("/:reservationId") {
    Services.authService.withUser { user =>() =>
      if (Services.purchase.purchase(user.getId, params("reservationId").toInt))
        Marshallers.ok()
      else
        Marshallers.bad("Cannot purchase reservation")
    }
  }

/*
  before() {
    if (!isAuthenticated)
      scentry.authenticate("RememberMe")

    requireLogin()
  }
*/

/*  get("/p/:page/c/:count", operation(getAll.notes("Require auth!"))) {
    purchaseService.findAll(uid, params("count").toInt, params("page").toInt).map(Purchase(_))
  }

  get("/awaiting_payment/p/:page/c/:count", operation(awaitingPayment)) {
    purchaseService.findAll(uid, params("count").toInt, params("page").toInt).filter(!_.sell.paid).map(Purchase(_))
  }

  get("/awaiting_feedback/p/:page/c/:count", operation(awaitingFeedback)) {
    purchaseService.findAll(uid, params("count").toInt, params("page").toInt).filter(_.feedbackId.isEmpty).map(Purchase(_))
  }

  get("/dispatched/p/:page/c/:count", operation(dispatched)) {
    purchaseService.findAll(uid, params("count").toInt, params("page").toInt).filter(_.trackingNumber.isDefined).map(Purchase(_))
  }*/
}

trait PurchaseOperations extends ApiDescription[Purchase] {
  override def name: String = "Purchase"

  override implicit def manifestForT: Manifest[Purchase] = Manifests.purchase

  val findAll = (apiOperation[List[Purchase]]("findAll")
    summary s"Get all ${name}s"
    notes s"Require auth!"
    parameters(pathParam[Int]("page").description(s"Page number to display"),
    pathParam[Int]("count").description(s"Items per page")))

  val awaitingPayment = (apiOperation[List[Purchase]]("awaiting_payment")
    summary s"Get Purchases awaiting payment."
    notes "Require auth!"
    parameters(pathParam[Int]("page").description(s"Page number to display"),
    pathParam[Int]("count").description(s"Items per page")))

  val awaitingFeedback = (apiOperation[List[Purchase]]("awaiting_feedback")
    summary s"Get Purchases awaiting feedback"
    notes "Require auth!"
    parameters(pathParam[Int]("page").description(s"Page number to display"),
    pathParam[Int]("count").description(s"Items per page")))

  val dispatched = (apiOperation[List[Purchase]]("dispatched")
    summary s"Get dispatched Purchases"
    notes "Purchases with trackingId. Require auth!"
    parameters(pathParam[Int]("page").description(s"Page number to display"),
    pathParam[Int]("count").description(s"Items per page")))
}
