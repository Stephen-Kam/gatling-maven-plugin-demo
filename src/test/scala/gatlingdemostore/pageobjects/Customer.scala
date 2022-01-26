package gatlingdemostore.pageobjects

import io.gatling.core.Predef._
import io.gatling.core.feeder.BatchableFeederBuilder
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object Customer {

  val loginFeeder: BatchableFeederBuilder[String]#F = csv("data/login.csv").random

  def login: ChainBuilder = {
    feed(loginFeeder)
      .exec(http("Load Login page")
        .get("/login")
        .check(
          status.is(200),
          substring("Username")))
      .exec(http("Login User")
        .post("/login")
        .formParam("_csrf", "${csrfValue}")
        .formParam("username", "${username}")
        .formParam("password", "${password}")
        .check(status.is(200)))
      .exec(session => session.set("customerLoggedIn", true))
      .exec { session =>
        //          println(session)
        session
      }
  }
}
