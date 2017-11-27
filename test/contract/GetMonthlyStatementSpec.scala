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

import common._
import org.scalatest.{Matchers, WordSpec}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.routing.Router
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.router.RoutesProvider

import scala.io.Source

class GetMonthlyStatementSpec
  extends WordSpec
  with Matchers
  with WSClientSetup
  with WiremockSetup
  with RestAssertions {

  override implicit lazy val app: Application =
    new GuiceApplicationBuilder().overrides(bind[Router].toProvider[RoutesProvider]).build()

  def getUriString(name: String): String =
    getClass.getResource(name).toURI.toString

  def getResourceAsString(name: String): String =
    Source.fromURL(getClass.getResource(name), "utf-8").mkString("")

  val monthlyStatementSchemaPath: String = getUriString("/public/api/conf/1.0/schemas/MonthlyStatement.get.schema.json")

  "/organisations/epaye/{ton}/{tor}/statements/{taxYear}/{taxMonth}" should {

    "returns a response body that conforms with the Monthly Statement schema" in {
      val empRef = EmpRef("921", "PE91702")

      val monthlyStatementUrl = s"$baseUrl/921/PE91702/statements/2017-18/3"

      val inputJsonString = getResourceAsString("/epaye/monthly-statement/in/921-PE91702-2017-3.json")

      given()
        .clientWith(empRef).isAuthorized
        .and().epayeMonthlyStatementReturns(inputJsonString)
        .when
        .get(monthlyStatementUrl).withAuthHeader()
        .thenAssertThat()
        .bodyIsOfSchema(monthlyStatementSchemaPath)
    }
  }

  "The provided example for the Monthly Statement" should {
    "conform to the schema" in {
      val monthlyStatementExampleJson = getResourceAsString("/public/api/conf/1.0/examples/MonthlyStatement.get.json")

      val report = Schema(monthlyStatementSchemaPath).validate(monthlyStatementExampleJson)

      withClue(report.toString) { report.isSuccess shouldBe true }
    }
  }
}

