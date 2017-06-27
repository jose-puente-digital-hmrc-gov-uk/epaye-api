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

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.connectors.EpayeConnector
import uk.gov.hmrc.epayeapi.models.Formats._
import uk.gov.hmrc.epayeapi.models.{ApiError, TotalsResponse}
import uk.gov.hmrc.epayeapi.models.api.{ApiJsonError, ApiSuccess}

import scala.concurrent.ExecutionContext

@Singleton
case class GetTotals @Inject() (
  authConnector: AuthConnector,
  epayeConnector: EpayeConnector,
  implicit val ec: ExecutionContext
)
  extends ApiController {

  def getTotals(empRef: EmpRef): Action[AnyContent] = EmpRefAction(empRef) { request =>
    epayeConnector.getTotals(empRef, hc(request)).map {
      case ApiSuccess(totals) =>
        Ok(Json.toJson(TotalsResponse(empRef, totals)))
      case ApiJsonError(err) =>
        Logger.error(s"Upstream returned invalid json: $err")
        InternalServerError(Json.toJson(ApiError.InternalServerError))
      case error =>
        Logger.error(s"Error while fetching totals: $error")
        InternalServerError(Json.toJson(ApiError.InternalServerError))
    }
  }
}
