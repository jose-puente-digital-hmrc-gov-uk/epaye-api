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

package uk.gov.hmrc.epayeapi.controllers

import java.util.NoSuchElementException
import javax.inject.{Inject, Singleton}

import akka.stream.Materializer
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, EssentialAction}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.connectors.EpayeConnector
import uk.gov.hmrc.epayeapi.models.Formats._
import uk.gov.hmrc.epayeapi.models.api.{ApiJsonError, ApiNotFound, ApiSuccess}
import uk.gov.hmrc.epayeapi.models.{ApiError, SummaryResponse}

import scala.concurrent.ExecutionContext

@Singleton
case class GetSummaryController @Inject()(
  authConnector: AuthConnector,
  epayeConnector: EpayeConnector,
  implicit val ec: ExecutionContext,
  implicit val mat: Materializer
)
  extends ApiController {

  def getSummary(empRef: EmpRef): EssentialAction = EmpRefAction(empRef) {
    Action.async { request =>
      val totalsResp = epayeConnector.getTotals(empRef, hc(request))
      val totalsByTypeResp = epayeConnector.getTotalsByType(empRef, hc(request))

      val resp = for {
        ApiSuccess(totals) <- totalsResp
        ApiSuccess(totalsByType) <- totalsByTypeResp
      } yield {
        Ok(Json.toJson(SummaryResponse(empRef, totals, totalsByType)))
      }

      // TODO: Revisit and add error handling!
      resp.recover {
        case ex: NoSuchElementException =>
          NotFound(Json.toJson(ApiError.EmpRefNotFound))
      }
    }
  }
}
