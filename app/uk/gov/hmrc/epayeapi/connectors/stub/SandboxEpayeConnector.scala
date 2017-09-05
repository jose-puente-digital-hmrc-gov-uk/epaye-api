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

package uk.gov.hmrc.epayeapi.connectors.stub

import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.connectors.EpayeConnector
import uk.gov.hmrc.epayeapi.models.api.{ApiNotFound, ApiResponse, ApiSuccess}
import uk.gov.hmrc.epayeapi.models.domain.{AggregatedTotals, AggregatedTotalsByType}
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

object SandboxEpayeConnector extends EpayeConnector {
  def getTotals(empRef: EmpRef, headers: HeaderCarrier): Future[ApiResponse[AggregatedTotals]] = {
    empRef match {
      case EmpRef("001", "0000001") =>
        Future.successful(ApiSuccess(AggregatedTotals(debit = 10000, credit = 0)))
      case EmpRef("002", "0000002") =>
        Future.successful(ApiSuccess(AggregatedTotals(debit = 0, credit = 10000)))
      case EmpRef("003", "0000003") =>
        Future.successful(ApiSuccess(AggregatedTotals(debit = 0, credit = 0)))
      case _ =>
        Future.successful(ApiNotFound[AggregatedTotals]())
    }
  }
  def getTotalsByType(empRef: EmpRef, headers: HeaderCarrier): Future[ApiResponse[AggregatedTotalsByType]] = {
    empRef match {
      case EmpRef("001", "0000001") =>
        Future.successful(ApiSuccess(AggregatedTotalsByType(rti = AggregatedTotals(debit = 10000, credit = 0))))
      case EmpRef("002", "0000002") =>
        Future.successful(ApiSuccess(AggregatedTotalsByType(rti = AggregatedTotals(debit = 0, credit = 10000))))
      case EmpRef("003", "0000003") =>
        Future.successful(ApiSuccess(AggregatedTotalsByType(rti = AggregatedTotals(debit = 0, credit = 0))))
      case _ =>
        Future.successful(ApiNotFound[AggregatedTotalsByType]())
    }
  }
}
