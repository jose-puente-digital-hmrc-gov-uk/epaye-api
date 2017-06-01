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

package controllers

import java.time.LocalDate
import java.util.{TimeZone, UUID}
import javax.inject.{Inject, Singleton}

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
case class RtiChargesController @Inject()(implicit val ec: ExecutionContext) extends Controller {

  def getCharges(): Action[AnyContent] = Action.async { implicit request =>
    Future.successful {
      Ok(Json.obj(
        "page" -> 1,
        "results" -> Seq(
          Json.obj(
            "main" -> "1010",
            "sub" -> "2020",
            "xref" -> Seq(UUID.randomUUID(),
                          UUID.randomUUID()),
            "name" -> "App Levy",
            "description" -> "App Levy charge",
            "outstandingAmount" -> BigDecimal("2013.27"),
            "accruedInterest" -> BigDecimal("30.78"),
            "totalAmount" -> BigDecimal("2044.05"),
            "isPenalty" -> false,
            "isInterest" -> false,
            "currency" -> "GBP",
            "dueDate" -> LocalDate.now(TimeZone.getTimeZone("BST").toZoneId).plusMonths(5),
            "_links" -> Seq.empty[String]
          )
        )
      ))
    }
  }
}
