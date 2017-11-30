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

import org.scalatest.{Matchers, WordSpec}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.routing.Router
import uk.gov.hmrc.epayeapi.router.RoutesProvider

import scala.io.Source

class GetAnnualStatementSpec
  extends WordSpec
  with Matchers
  with WSClientSetup
  with WiremockSetup
  with EmpRefGenerator
  with RestAssertions {

  override implicit lazy val app: Application =
    new GuiceApplicationBuilder().overrides(bind[Router].toProvider[RoutesProvider]).build()

  val annualStatementSchemaPath: String =
    s"${app.path.toURI}/resources/public/api/conf/1.0/schemas/AnnualStatement.get.schema.json"

  "/organisations/epaye/{ton}/{tor}/statements/{taxYear}" should {

    "returns a response body that conforms with the Annual Statement schema" in {

      val empRef = randomEmpRef()

      val annualStatementUrl =
        s"$baseUrl/${empRef.taxOfficeNumber}/${empRef.taxOfficeReference}/statements/2016-17"

      given()
        .clientWith(empRef).isAuthorized
        .and().epayeAnnualStatementReturns(Fixtures.epayeAnnualStatement)
        .when
        .get(annualStatementUrl).withAuthHeader()
        .thenAssertThat()
        .bodyIsOfSchema(annualStatementSchemaPath)
    }
  }

  "The provided example for the Annual Statement" should {
    "conform to the schema" in {
      val annualStatementExampleJson: String =
        Source.fromURL(s"${app.path.toURI}/resources/public/api/conf/1.0/examples/AnnualStatement.get.json")
          .getLines.mkString

      val report = Schema(annualStatementSchemaPath).validate(annualStatementExampleJson)

      withClue(report.toString) { report.isSuccess shouldBe true }
    }
  }
}

