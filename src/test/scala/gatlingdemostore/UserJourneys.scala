package gatlingdemostore

import gatlingdemostore.pageobjects.Catalog.Product
import gatlingdemostore.pageobjects.{Catalog, Checkout, CmsPages}
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

import scala.concurrent.duration.{FiniteDuration, _}

object UserJourneys extends Properties {

  def minPause: FiniteDuration = 100.milliseconds

  def maxPause: FiniteDuration = 500.milliseconds

  def browseStore: ChainBuilder = {
    exec(initSession)
      .exec(CmsPages.homepage)
      .pause(maxPause)
      .exec(CmsPages.aboutUs)
      .pause(minPause, maxPause)
      .repeat(5) {
        exec(Catalog.Category.view)
          .pause(minPause, maxPause)
          .exec(Product.view)
      }
  }

  def abandonCart: ChainBuilder = {
    exec(initSession)
      .exec(CmsPages.homepage)
      .pause(maxPause)
      .exec(Catalog.Category.view)
      .pause(minPause, maxPause)
      .exec(Product.view)
      .pause(minPause, maxPause)
      .exec(Product.add)
  }

  def completePurchase: ChainBuilder = {
    exec(initSession)
      .exec(CmsPages.homepage)
      .pause(maxPause)
      .exec(Catalog.Category.view)
      .pause(minPause, maxPause)
      .exec(Product.view)
      .pause(minPause, maxPause)
      .exec(Product.add)
      .pause(minPause, maxPause)
      .exec(Checkout.view)
      .pause(minPause, maxPause)
      .exec(Checkout.completeCheckout)
  }
}
