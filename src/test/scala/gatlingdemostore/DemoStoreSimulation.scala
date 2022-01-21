package gatlingdemostore

import io.gatling.core.Predef._
import io.gatling.core.feeder.{BatchableFeederBuilder, FileBasedFeederBuilder}
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.util.Random

class DemoStoreSimulation extends Simulation {

  val domain = "demostore.gatling.io"

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(s"http://$domain")

  val categoryFeeder: BatchableFeederBuilder[String]#F = csv("data/categoryDetails.csv").random
  val loginFeeder: BatchableFeederBuilder[String]#F = csv("data/login.csv").random
  val jsonFeederProducts: FileBasedFeederBuilder[Any]#F = jsonFile("data/productDetails.json").random

  val rnd = new Random()

  def randomString(length: Int): String = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  val initSession: ChainBuilder =
    exec(flushCookieJar)
      .exec(session => session.set("randomNumber", rnd.nextInt))
      .exec(session => session.set("customerLoggedIn", false))
      .exec(session => session.set("cartTotal", 0.00))
      .exec(addCookie(Cookie("sessionId", randomString(10)).withDomain(domain)))
      .exec { session => println(session); session }

  object CmsPages {
    def homepage: ChainBuilder = {
      exec(http("Load Home page")
        .get("/")
        .check(
          status.is(200),
          regex("<h2>Welcome to the Gatling DemoStore!</h2>").exists,
          css("#_csrf", "content").saveAs("csrfValue")))
    }

    def aboutUs: ChainBuilder = {
      exec(http("Load About Us page")
        .get("/about-us")
        .check(
          status.is(200),
          regex("<h2>About Us</h2>").exists))
    }
  }

  object Catalog {

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
            println(s"currentCartTotal: $currentCartTotal \n itemPrice: $itemPrice")
            session.set("cartTotal", (currentCartTotal + itemPrice))
          })
      }
    }

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

  }

  object Customer {
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
          println(session)
          session
        }
    }
  }

  val scn: ScenarioBuilder = scenario("DemoStoreSimulation")
    .exec(initSession)
    .exec(CmsPages.homepage)
    .pause(2)
    .exec(CmsPages.aboutUs)
    .pause(2)
    .exec(Catalog.Category.view)
    .pause(2)
    .exec(Catalog.Product.view)
    .pause(2)
    .exec(Catalog.Product.add)
    .pause(2)
    .exec(Catalog.Checkout.view)
    .pause(2)
    .exec(Catalog.Checkout.completeCheckout)

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}