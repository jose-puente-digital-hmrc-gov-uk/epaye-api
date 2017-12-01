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

package integration

import contract._
import org.scalatest.{Matchers, WordSpec}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.routing.Router
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.router.RoutesProvider

class GetAnnualStatementSpec extends WordSpec
  with Matchers
  with WSClientSetup
  with WiremockSetup
  with EmpRefGenerator
  with RestAssertions {

  override implicit lazy val app: Application =
    new GuiceApplicationBuilder().overrides(bind[Router].toProvider[RoutesProvider]).build()

  "AnnualStatement API should return a statement " should {
    "containing EYU data" in {
      val empRef = EmpRef("840", "GZ00064")

      val annualStatementUrl =
        s"$baseUrl/${empRef.taxOfficeNumber}/${empRef.taxOfficeReference}/statements/2017-18"

      val expectedResponse = Json.parse("""
        |{
        |  "taxYear": {
        |    "year": "2017-18",
        |    "firstDay": "2017-04-06",
        |    "lastDay": "2018-04-05"
        |  },
        |  "nonRtiCharges": [
        |    {
        |      "code": "NON_RTI_CIS_FIXED_PENALTY",
        |      "taxPeriod": {
        |        "firstDay": "2017-04-06",
        |        "lastDay": "2018-04-05"
        |      },
        |      "amount": 300,
        |      "clearedByCredits": 70,
        |      "clearedByPayments": 30,
        |      "balance": 200,
        |      "dueDate": "2017-07-22"
        |    }
        |  ],
        |  "_embedded": {
        |    "earlierYearUpdate": {
        |      "amount": 700,
        |      "clearedByCredits": 200,
        |      "clearedByPayments": 300,
        |      "balance": 200,
        |      "dueDate": "2017-04-22"
        |    },
        |    "rtiCharges": [
        |      {
        |        "taxMonth": {
        |          "number": 7,
        |          "firstDay": "2017-10-06",
        |          "lastDay": "2017-11-05"
        |        },
        |        "amount": 1200,
        |        "clearedByCredits": 0,
        |        "clearedByPayments": 0,
        |        "balance": 1200,
        |        "dueDate": "2017-11-22",
        |        "isSpecified": false,
        |        "_links": {
        |          "self": {
        |            "href": "/organisations/paye/840/GZ00064/statements/2017-18/7"
        |          }
        |        }
        |      },
        |      {
        |        "taxMonth": {
        |          "number": 3,
        |          "firstDay": "2017-06-06",
        |          "lastDay": "2017-07-05"
        |        },
        |        "amount": 700,
        |        "clearedByCredits": 200,
        |        "clearedByPayments": 300,
        |        "balance": 200,
        |        "dueDate": "2017-07-22",
        |        "isSpecified": true,
        |        "_links": {
        |          "self": {
        |            "href": "/organisations/paye/840/GZ00064/statements/2017-18/3"
        |          }
        |        }
        |      }
        |    ]
        |  },
        |  "_links": {
        |    "empRefs": {
        |      "href": "/organisations/paye"
        |    },
        |    "statements": {
        |      "href": "/organisations/paye/840/GZ00064/statements"
        |    },
        |    "self": {
        |      "href": "/organisations/paye/840/GZ00064/statements/2017-18"
        |    },
        |    "next": {
        |      "href": "/organisations/paye/840/GZ00064/statements/2018-19"
        |    },
        |    "previous": {
        |      "href": "/organisations/paye/840/GZ00064/statements/2016-17"
        |    }
        |  }
        |}
      """.stripMargin
      )

      given()
        .clientWith(empRef).isAuthorized
        .and().epayeAnnualStatementReturns(Fixtures.epayeAnnualStatementWithEyu)
        .when()
        .get(annualStatementUrl).withAuthHeader()
        .thenAssertThat()
        .bodyIsOfJson(expectedResponse)
    }
  }
}
