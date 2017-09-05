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

package uk.gov.hmrc.epayeapi.models

import play.api.libs.json._
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.epayeapi.models.domain.{AggregatedTotals, AggregatedTotalsByType}

trait Formats {
  implicit val empRefFormat: Writes[EmpRef] = new Writes[EmpRef] {
    override def writes(o: EmpRef): JsValue = JsString(s"${o.taxOfficeNumber}/${o.taxOfficeReference}")
  }
  implicit val linkFormat: Format[Link] = Json.format[Link]
  implicit val empRefLinksFormat: Format[EmpRefLinks] = Json.format[EmpRefLinks]
  implicit val empRefItemFormat: Format[EmpRefItem] = Json.format[EmpRefItem]
  implicit val empRefsResponseFormat: Format[EmpRefsResponse] = Json.format[EmpRefsResponse]
  implicit val apiErrorFormat: Format[ApiError] = Json.format[ApiError]
  implicit val aggregatedTotalsFormat: Format[AggregatedTotals] = Json.format[AggregatedTotals]
  implicit val aggregatedTotalsByTypeFormat: Format[AggregatedTotalsByType] = Json.format[AggregatedTotalsByType]
  implicit val totalsLinksFormat: Format[TotalsLinks] = Json.format[TotalsLinks]
  implicit val totalsResponseFormat: Format[TotalsResponse] = Json.format[TotalsResponse]
  implicit val totalsByTypeResponseFormat: Format[TotalsByTypeResponse] = Json.format[TotalsByTypeResponse]
}

object Formats extends Formats
