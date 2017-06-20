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
import java.util
import java.util.{TimeZone, UUID}
import javax.inject.{Inject, Singleton}

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

@Singleton
case class RtiChargesController @Inject() (implicit val ec: ExecutionContext) extends BaseController {

  private def getRandomCharge = {
    val outstandingAmount = BigDecimal(Random.nextFloat() * 10000).setScale(2, BigDecimal.RoundingMode.HALF_UP)
    val interest = BigDecimal(Random.nextFloat() * 100).setScale(2, BigDecimal.RoundingMode.HALF_UP)
    val total = outstandingAmount + interest
    val links = Json.obj(
      "self" -> s"/epaye/self/charge/${UUID.randomUUID()}"
    )

    Json.obj(
      "main" -> "1010",
      "sub" -> "2020",
      "xref" -> Seq(
        UUID.randomUUID(),
        UUID.randomUUID()
      ),
      "name" -> "App Levy",
      "description" -> "App Levy charge",
      "outstandingAmount" -> outstandingAmount,
      "accruedInterest" -> interest,
      "totalAmount" -> total,
      "isPenalty" -> false,
      "isInterest" -> false,
      "currency" -> "GBP",
      "dueDate" -> LocalDate.now(TimeZone.getTimeZone("BST").toZoneId).plusMonths(5),
      "_links" -> links
    )
  }

  def getCharges(): Action[AnyContent] = Action.async { implicit request =>
    Future.successful {
      Ok(Json.obj(
        "page" -> 1,
        "results" -> (for (_ <- 1 to 10) yield getRandomCharge).toSeq
      ))
    }
  }
}
