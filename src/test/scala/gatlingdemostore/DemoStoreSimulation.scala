package gatlingdemostore

import gatlingdemostore.pageobjects.Catalog.Product
import gatlingdemostore.pageobjects._
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

class DemoStoreSimulation extends Simulation with Properties {

  before {
    println(s"Running test with $userCount users")
    println(s"Ramping users over $rampDuration seconds")
    println(s"Total test duration: $testDuration seconds")
  }

  after {
    println("Stress testing complete")
  }

  //      .exec { session => println(session); session }

  val scn: ScenarioBuilder = scenario("DemoStoreSimulation")
    .exec(initSession)
    .exec(CmsPages.homepage)
    .pause(2)
    .exec(CmsPages.aboutUs)
    .pause(2)
    .exec(Catalog.Category.view)
    .pause(2)
    .exec(Product.view)
    .pause(2)
    .exec(Product.add)
    .pause(2)
    .exec(Checkout.view)
    .pause(2)
    .exec(Checkout.completeCheckout)


  //  setUp(
  //    scn.inject(
  //      atOnceUsers(3),
  //      nothingFor(5.seconds),
  //      rampUsers(10) during (20.seconds),
  //      nothingFor(10.seconds),
  //      constantUsersPerSec(1) during (20.seconds)
  //    ).protocols(httpProtocol))

  //  setUp(
  //    scn.inject(
  //      constantConcurrentUsers(10) during (20.seconds),
  //      rampConcurrentUsers(10) to 20 during 20.seconds
  //    ).protocols(httpProtocol)
  //  )

  //  setUp(
  //    scn.inject(
  //      constantUsersPerSec(1) during 3.minutes
  //    ).protocols(httpProtocol).throttle(
  //      reachRps(10) in 30.seconds,
  //      holdFor(60.seconds),
  //      jumpToRps(20),
  //      holdFor(60.seconds)
  //    )
  //  ).maxDuration(3.minutes)

  //  setUp(
  //    Scenarios.default
  //      .inject(rampUsers(userCount) during (rampDuration.seconds))
  //      .protocols(httpProtocol)
  //  )

  setUp(
    Scenarios.default
      .inject(rampUsers(userCount) during (rampDuration.seconds))
      .protocols(httpProtocol)
      .andThen(
        Scenarios.highPurchase
          .inject(rampUsers(2) during (10.seconds))
          .protocols(httpProtocol)
      )
  )
}