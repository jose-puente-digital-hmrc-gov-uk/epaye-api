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

package uk.gov.hmrc.epayeapi.models.out

import play.api.libs.json.Json.format
import play.api.libs.json._
import uk.gov.hmrc.domain.EmpRef

trait Formats {
  implicit lazy val empRefFormat: Writes[EmpRef] = new Writes[EmpRef] {
    override def writes(o: EmpRef): JsValue = JsString(s"${o.taxOfficeNumber}/${o.taxOfficeReference}")
  }
  implicit lazy val linkFormat: Format[Link] = format[Link]

  implicit lazy val empRefsLinksFormat: Format[EmpRefsLinks] = format[EmpRefsLinks]
  implicit lazy val empRefLinksFormat: Format[EmpRefLinks] = format[EmpRefLinks]
  implicit lazy val empRefItemFormat: Format[EmpRefItem] = format[EmpRefItem]
  implicit lazy val empRefsResponseFormat: Format[EmpRefsResponse] = format[EmpRefsResponse]
  implicit lazy val breakdown: Format[Breakdown] = format[Breakdown]
  implicit lazy val outstandingCharges: Format[OutstandingCharges] = format[OutstandingCharges]
  implicit lazy val totalsLinksFormat: Format[SummaryLinks] = format[SummaryLinks]
  implicit lazy val totalsResponseFormat: Format[SummaryResponse] = format[SummaryResponse]
  implicit lazy val apiErrorFormat: Format[ApiError] = format[ApiError]
}

object Formats extends Formats
