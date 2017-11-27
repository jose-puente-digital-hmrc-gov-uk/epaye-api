/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package contract

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Matchers, WordSpec}
import org.scalatestplus.play.OneServerPerSuite
import play.api.libs.ws.WSClient
import uk.gov.hmrc.play.http.HeaderCarrier

trait WiremockSetup
  extends WordSpec
    with Matchers
    with OneServerPerSuite
    with Eventually
    with ScalaFutures
    with BeforeAndAfterEach
    with IntegrationPatience
    with BeforeAndAfterAll
    with RestAssertions {

  val baseUrl = s"http://localhost:$port"

  implicit val hc = HeaderCarrier()

  implicit val wsClient: WSClient = app.injector.instanceOf[WSClient]

  lazy val WIREMOCK_PORT: Int = 22222

  protected val wiremockBaseUrl: String = s"http://localhost:$WIREMOCK_PORT"
  val wireMockServer = new WireMockServer(wireMockConfig().port(WIREMOCK_PORT))


  override def beforeAll(): Unit = {
    wireMockServer.stop()
    wireMockServer.start()
    WireMock.configureFor("localhost", WIREMOCK_PORT)
  }

  override def beforeEach(): Unit = {
    WireMock.reset()
  }

}


