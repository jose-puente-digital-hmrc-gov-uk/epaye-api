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

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, stubFor, urlPathEqualTo}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.routing.Router
import uk.gov.hmrc.epayeapi.router.RoutesProvider
import play.api.inject.bind

class TotalsSpec extends WiremockSetup with EmpRefGenerator with RestAssertions {

  override implicit lazy val app: Application =
    new GuiceApplicationBuilder().overrides(bind[Router].toProvider[RoutesProvider]).build()

  "/organisation/epaye/{ton}/{tor}/" should {
    // TODO: investigate a library to write contract tests and rewrite this test with it
    //    "return a json with fields 'owed' and '_links'" in {
    //      val empRef = randomEmpRef()
    //
    //      given()
    //        .client(empRef).isAuthorized
    //        .epayeTotalsReturns(owed = 200)
    //        .when()
    //        .getTotalsFor(empRef).withAuthHeader()
    //        .thenAssertThat()
    //        .statusCodeIs(200)
    //        .bodyIs("""{"owed":2000,"_links":{"empRefs":{"href":"/paye-for-employers/"}}}""")
    ////        .bodyIsOfSchema("Totals.get.schema.json")
    //    }

//    "return the same fields as specified in the specification" in {
//      val empRef = randomEmpRef()
//
//      val summaryUrl = s"$baseUrl/${empRef.taxOfficeNumber}/${empRef.taxOfficeReference}/"
//
//      // GIVEN
//      // Authorised
//      stubFor(get(urlPathEqualTo(s"/authorise/read/epaye/${empRef.encodedValue}")).willReturn(aResponse().withStatus(200)))
//
//      // EPAYE returns
//      val response = aResponse()
//      response
//        .withBody(s""" {"credit": 100, "debit": 1000} """)
//        .withHeader("Content-Type", "application/json")
//
//      stubFor(get(urlPathEqualTo(s"/epaye/${empRef.encodedValue}/api/v1/totals"))
//        .willReturn(response.withStatus(200)))
//    }

    "return 200 OK on active enrolments given no debit" in {
      val empRef = randomEmpRef()

      val totalsUrl =
        s"$baseUrl/${empRef.taxOfficeNumber}/${empRef.taxOfficeReference}/"

      given()
        .clientWith(empRef).isAuthorized
        .and().epayeTotalsReturns(owed = 0)
        .when
        .get(totalsUrl).withAuthHeader()
        .thenAssertThat()
        .statusCodeIs(200)

      //        .bodyIs("""{"owed":2000,"_links":{"empRefs":{"href":"/paye-for-employers/"}}}""")
      //        .bodyIsOfSchema("Totals.get.schema.json")
    }

  }
}

//given()
//.client(empRef).isAuthorized
//.masterDataApiReturns(200, Some(masterDataJson(empRef.toString, "MONTHLY").toString))
//.chargesApiReturns(200, Some(financialTransactionsJson(
//empRef = empRef.toString,
//desCharges = Seq(
////            FPS charges
//DesChargeDetailJson("2000", "1000", original = 10, outstanding = 10, taxFrom = "2015-05-06", taxTo = "2015-06-05", dueDate = "2015-06-22"),
//DesChargeDetailJson("2000", "1020", 20, 20, "2015-05-06", "2015-06-05", dueDate = "2015-06-22"),
////            CIS charges
//DesChargeDetailJson("2040", "1000", 30, 30, "2015-05-06", "2015-06-05", "2015-06-22"),
////            EPS charges
//DesChargeDetailJson("2030", "1260", 40, 40, "2015-05-06", "2015-06-05", "2015-06-22"),
//DesChargeDetailJson("2030", "1270", 50, 50, "2015-05-06", "2015-06-05", "2015-06-22"),
////            Other charges
//DesChargeDetailJson("2005", "2000", 60, 60, "2015-05-06", "2015-06-05", "2015-06-22"),
//DesChargeDetailJson("2135", "2355", 70, 70, "2015-05-06", "2015-06-05", "2015-06-22"),
////            Non Rti charges to be ignored
//DesChargeDetailJson("2761", "2090", 80, 80, "2015-05-06", "2015-06-05", "2015-06-22"),
//DesChargeDetailJson("2762", "1090", 90, 90, "2015-05-06", "2015-06-05", "2015-06-22")
//)
//).toString))
//.when()
//.get(url).withAuthHeader()
//.thenAssertThat()
//.statusCodeIs(200)
//.bodyIsJson(monthlyStatementJson(
//fpsCharges = Seq(
//MonthlyStatementItemJson("2000", "1000", 10),
//MonthlyStatementItemJson("2000", "1020", 20)
//),
//cisCharges = Seq(
//MonthlyStatementItemJson("2040", "1000", 30)
//),
//epsCharges = Seq(
//MonthlyStatementItemJson("2030", "1260", 40),
//MonthlyStatementItemJson("2030", "1270", 50)
//),
//other = 60 + 70,
//fpsCredits = Seq.empty,
//cisCredits = Seq.empty,
//epsCredits = Seq.empty,
//payments = Seq.empty,
//dueDate = Some("2015-06-22")
//))