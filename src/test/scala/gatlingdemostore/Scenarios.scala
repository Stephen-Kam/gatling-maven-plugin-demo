package gatlingdemostore

import io.gatling.core.Predef.{exec, randomSwitch, scenario}
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

object Scenarios extends Properties {

  def default: ScenarioBuilder = scenario("Default Load Test")
    .during(testDuration.seconds) {
      randomSwitch(
        75d -> exec(UserJourneys.browseStore),
        15d -> exec(UserJourneys.abandonCart),
        10d -> exec(UserJourneys.completePurchase)
      )
    }

  def highPurchase: ScenarioBuilder = scenario("High Purchase Load Test")
    .during(testDuration.seconds) {
      randomSwitch(
        25d -> exec(UserJourneys.browseStore),
        25d -> exec(UserJourneys.abandonCart),
        50d -> exec(UserJourneys.completePurchase)
      )
    }

}
