package gatlingdemostore.pageobjects

import io.gatling.core.Predef._
import io.gatling.core.feeder.{BatchableFeederBuilder, FileBasedFeederBuilder}
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object Catalog {

  val categoryFeeder: BatchableFeederBuilder[String]#F = csv("data/categoryDetails.csv").random
  val jsonFeederProducts: FileBasedFeederBuilder[Any]#F = jsonFile("data/productDetails.json").random


  object Category {
    def view: ChainBuilder = {
      feed(categoryFeeder)
        .exec(http("Load Category page - ${categoryName}")
          .get("/category/${categorySlug}")
          .check(
            status.is(200),
            css("#CategoryName").is("${categoryName}")))
    }
  }


  object Product {
    def view: ChainBuilder = {
      feed(jsonFeederProducts)
        .exec(http("Load Product page - ${name}")
          .get("/product/${slug}")
          .check(status.is(200),
            css("#ProductDescription").is("${description}")))
    }

    def add: ChainBuilder = {
      exec(http("Add Product to Cart")
        .get("/cart/add/${id}")
        .check(
          status.is(200),
          substring("items in your cart")))
        .exec(session => {
          val currentCartTotal = session("cartTotal").as[Double]
          val itemPrice = session("price").as[Double]
          //            println(s"currentCartTotal: $currentCartTotal \n itemPrice: $itemPrice")
          session.set("cartTotal", (currentCartTotal + itemPrice))
        })
    }
  }

}
