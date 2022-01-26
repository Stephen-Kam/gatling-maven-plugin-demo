package gatlingdemostore

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.{Cookie, addCookie, flushCookieJar, _}
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.util.Random

trait Properties {

  def userCount: Int = getProperty("USERS", "5").toInt

  def rampDuration: Int = getProperty("RAMP_DURATION", "10").toInt

  def testDuration: Int = getProperty("DURATION", "60").toInt

  def randomString(length: Int): String = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  val rnd = new Random()

  val domain = "demostore.gatling.io"

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(s"http://$domain")

  private def getProperty(propertyName: String, defaultValue: String): String = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  val initSession: ChainBuilder =
    exec(flushCookieJar)
      .exec(session => session.set("randomNumber", rnd.nextInt))
      .exec(session => session.set("customerLoggedIn", false))
      .exec(session => session.set("cartTotal", 0.00))
      .exec(addCookie(Cookie("sessionId", randomString(10)).withDomain(domain)))

}
