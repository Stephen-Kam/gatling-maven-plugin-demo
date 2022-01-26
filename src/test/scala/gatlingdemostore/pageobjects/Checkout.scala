package gatlingdemostore.pageobjects

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object Checkout {

  def view: ChainBuilder = {
    doIf(session => !session("customerLoggedIn").as[Boolean]) {
      exec(Customer.login)
    }
      .exec(http("Load Cart page")
        .get("/cart/view")
        .check(
          status.is(200),
          css("#grandTotal").is("$$${cartTotal}")))
  }

  def completeCheckout: ChainBuilder = {
    exec(http("Checkout Cart")
      .get("/cart/checkout")
      .check(
        status.is(200),
        substring("Thanks for your order! See you soon!")))
  }
}
