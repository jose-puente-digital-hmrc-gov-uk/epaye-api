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

package unit

import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatest.{OptionValues, ShouldMatchers, fixture}
import org.scalatestplus.play.{MixedFixtures, WsScalaTestClient}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.auth.core.AuthConnector

import scala.reflect.ClassTag

abstract class AppSpec
  extends fixture.WordSpec
  with ShouldMatchers
  with MockitoSugar
  with ScalaFutures
  with OptionValues
  with MixedFixtures
  with Eventually
  with IntegrationPatience
  with WsScalaTestClient
  with FakeAuthConnector {
  def inject[A: ClassTag](implicit a: Application): A = a.injector.instanceOf[A]

  def build(auth: AuthConnector): Application =
    GuiceApplicationBuilder()
      .overrides(bind(classOf[AuthConnector]).toInstance(auth))
      .build

  case class Builder(builder: GuiceApplicationBuilder) {
    def update(fn: GuiceApplicationBuilder => GuiceApplicationBuilder): Builder = copy(fn(builder))
    def withAuth(connector: AuthConnector): Builder =
      update(_.overrides(bind(classOf[AuthConnector]).toInstance(connector)))
    def build: Application = builder.build()
  }

  def builder: Builder = Builder(GuiceApplicationBuilder())
}
