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

import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.Matchers
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.mvc.Results
import play.api.test.FakeHeaders
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.play.http.ws.WSHttpResponse
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class Http(wsClient: WSClient) {

  def get(url: String)(implicit hc: HeaderCarrier): HttpResponse = perform(url) { request =>
    request.get()
  }

  def post[A](url: String, body: A, headers: Seq[(String, String)] = Seq.empty)(implicit writes: Writes[A], hc: HeaderCarrier): HttpResponse = perform(url) { request =>
    request.post(Json.toJson(body))
  }

  def postEmpty(url: String)(implicit hc: HeaderCarrier): HttpResponse = perform(url) { request =>
    request.post(Results.EmptyContent())
  }

  def delete(url: String)(implicit hc: HeaderCarrier): HttpResponse = perform(url) { request =>
    request.delete()
  }

  private def perform(url: String)(fun: WSRequest => Future[WSResponse])(implicit hc: HeaderCarrier): WSHttpResponse =
    await(fun(wsClient.url(url).withHeaders(hc.headers: _*).withRequestTimeout(Duration(2, SECONDS))).map(new WSHttpResponse(_)))

  private def await[A](future: Future[A]) = {
    Await.result(future, Duration(10, SECONDS))
  }

}

class Assertions(response: HttpResponse) extends Matchers {
  def bodyIsOfSchema(schema: String): Unit = {
    ???
  }

  def bodyIsJson(json: JsValue) = {
    Json.parse(response.body) shouldEqual json
    this
  }

  def bodyContainsJson[A](tuple: (String, A))(implicit reader: Reads[A]) = {
    (response.json \ tuple._1).validate[A] should equal(JsSuccess(tuple._2))
    this
  }


  def bodyContainsJsonField(fieldName: String): Assertions = {
    response.json.as[JsObject].keys should contain(fieldName)
    this
  }

  def bodyIs(expectedBody: String): Unit = {
    response.body shouldBe expectedBody
  }

  def statusCodeIs(statusCode: Int): Assertions = {
    response.status shouldBe statusCode
    this
  }

  def upstreamRequestMatchesExtendedDesApiHeaders(urlPathPattern: String): Assertions = {
    val additionalHeaders = Seq(("acknowledgementReference", "\\A[A-Za-z0-9]{32}\\Z"))
    upstreamRequestMatchesDesApiHeaders(urlPathPattern, additionalHeaders)
  }

  def upstreamRequestMatchesDesApiHeaders(urlPathPattern: String, additionalHeaders: Seq[(String, String)]): Assertions = {
    val desApiHeaders = Seq(("Authorization", "\\ABearer epaye-auth-header-test-value\\Z"), ("Environment", "\\Aepaye-hod-test-environment\\Z"))
    val requiredHeaders = desApiHeaders ++ additionalHeaders
    upstreamRequestMatchesHeaders(urlPathPattern, requiredHeaders)
  }

  def upstreamRequestMatchesHeaders(urlPathPattern: String, requiredHeaders: Seq[(String, String)]): Assertions = {
    val requestedFor = getRequestedFor(urlPathMatching(urlPathPattern))
    requiredHeaders.iterator foreach (header => requestedFor.withHeader(header._1, matching(header._2)))
    verify(requestedFor)
    this
  }

}

class HttpExecutor(method: String, url: String)(implicit http: Http) {
  var headers = new ListBuffer[(String, String)]

  lazy val hc: HeaderCarrier = HeaderCarrier.fromHeadersAndSession(FakeHeaders(headers))

  def withAuthHeader(): HttpExecutor = {
    headers.append(("Authorization", "foobar"))
    this
  }

  def thenAssertThat(): Assertions = new Assertions(http.get(url)(hc))

}

class HttpVerbs(wsClient: WSClient) {
  def get(url: String): HttpExecutor = {
    new HttpExecutor("GET", url)(new Http(wsClient))
  }
}

class ClientGivens(empRef: EmpRef) {
  def epayeTotalsReturns(owed: BigDecimal): ClientGivens = {
    val response = aResponse()

    response
      .withBody( s""" {"credit": 100, "debit": $owed} """)
      .withHeader("Content-Type", "application/json")


//    s"${config.baseUrl}" +
//      s"/epaye" +
//      s"/${empRef.encodedValue}" +
//      s"/api/v1/totals"

    stubFor(get(urlPathEqualTo(s"/epaye/${empRef.encodedValue}/api/v1/totals"))
      .willReturn(response.withStatus(200)))

    this
  }

  def and(): ClientGivens = this

  def isUnauthorized: ClientGivens = {
    // /authorise/read/epaye/api?....
    // /authorise/read/epaye/$empref
    stubFor(get(urlPathEqualTo(s"/authorise/read/epaye/${empRef.encodedValue}")).willReturn(aResponse().withStatus(404)))
    this
  }

  def isAuthorized: ClientGivens = {
    stubFor(get(urlPathEqualTo(s"/authorise/read/epaye/${empRef.encodedValue}")).willReturn(aResponse().withStatus(200)))
    this
  }

  def when()(implicit wsClient: WSClient): HttpVerbs = new HttpVerbs(wsClient)

}

class Givens {
  def client(empRef: EmpRef): ClientGivens = new ClientGivens(empRef)
}

trait RestAssertions {
  protected def given() = new Givens()
}

