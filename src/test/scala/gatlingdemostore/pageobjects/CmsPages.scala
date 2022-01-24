package gatlingdemostore.pageobjects

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

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
