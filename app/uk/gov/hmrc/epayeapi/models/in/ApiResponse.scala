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

package uk.gov.hmrc.epayeapi.models.in

import play.api.libs.json.JsError

sealed trait ApiResponse[A]
case class ApiSuccess[A](obj: A) extends ApiResponse[A]
case class ApiJsonError[A](error: JsError) extends ApiResponse[A]
case class ApiNotFound[A]() extends ApiResponse[A]
case class ApiError[A](status: Int, body: String) extends ApiResponse[A]
case class ApiException[A](message: String) extends ApiResponse[A]
