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

package common

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.domain.EmpRef

object Fixtures {

  val epayeAnnualStatement: String =
    """
      |{
      |  "rti": {
      |    "lineItems": [
      |      {
      |        "taxYear": {
      |          "yearFrom": 2017
      |        },
      |        "taxMonth": {
      |          "month": 7
      |        },
      |        "charges": {
      |          "debit": 1200,
      |          "credit": 0
      |        },
      |        "cleared": {
      |          "cleared": 0,
      |          "payment": 0,
      |          "credit": 0
      |        },
      |        "balance": {
      |          "debit": 1200,
      |          "credit": 0
      |        },
      |        "dueDate": "2017-11-22",
      |        "isSpecified": false,
      |        "itemType": "month"
      |      },
      |      {
      |        "taxYear": {
      |          "yearFrom": 2017
      |        },
      |        "charges": {
      |          "debit": 700,
      |          "credit": 0
      |        },
      |        "cleared": {
      |          "cleared": 0,
      |          "payment": 300,
      |          "credit": 200
      |        },
      |        "balance": {
      |          "debit": 200,
      |          "credit": 0
      |        },
      |        "dueDate": "2017-04-22",
      |        "isSpecified": false,
      |        "itemType": "eyu"
      |      },
      |      {
      |        "taxYear": {
      |          "yearFrom": 2017
      |        },
      |        "taxMonth": {
      |          "month": 3
      |        },
      |        "charges": {
      |          "debit": 700,
      |          "credit": 0
      |        },
      |        "cleared": {
      |          "cleared": 0,
      |          "payment": 300,
      |          "credit": 200
      |        },
      |        "balance": {
      |          "debit": 200,
      |          "credit": 0
      |        },
      |        "dueDate": "2017-07-22",
      |        "isSpecified": true
      |      }
      |    ],
      |    "totals": {
      |      "charges": {
      |        "debit": 1900,
      |        "credit": 0
      |      },
      |      "cleared": {
      |        "cleared": 0,
      |        "payment": 300,
      |        "credit": 200
      |      },
      |      "balance": {
      |        "debit": 1400,
      |        "credit": 0
      |      }
      |    }
      |  },
      |  "nonRti": {
      |    "lineItems": [
      |      {
      |        "taxYear": {
      |          "yearFrom": 2017
      |        },
      |        "charges": {
      |          "debit": 300,
      |          "credit": 0
      |        },
      |        "cleared": {
      |          "cleared": 100,
      |          "payment": 30,
      |          "credit": 70
      |        },
      |        "balance": {
      |          "debit": 200,
      |          "credit": 0
      |        },
      |        "dueDate": "2017-07-22",
      |        "isSpecified": false,
      |        "itemType": "1481",
      |        "codeText": "NON_RTI_CIS_FIXED_PENALTY"
      |      }
      |    ],
      |    "totals": {
      |      "charges": {
      |        "debit": 0,
      |        "credit": 0
      |      },
      |      "cleared": {
      |        "cleared": 0,
      |        "payment": 0,
      |        "credit": 0
      |      },
      |      "balance": {
      |        "debit": 0,
      |        "credit": 0
      |      }
      |    }
      |  },
      |  "unallocated": 2000
      |}
    """.stripMargin

  def expectedAnnualStatementJson(apiBaseUrl: String, empRef: EmpRef): JsValue = Json.parse(
    s"""
      |{
      |  "taxYear": {
      |    "year": "2017-18",
      |    "firstDay": "2017-04-06",
      |    "lastDay": "2018-04-05"
      |  },
      |  "nonRtiCharges": [
      |    {
      |      "code": "NON_RTI_CIS_FIXED_PENALTY",
      |      "amount": 300,
      |      "clearedByCredits": 70,
      |      "clearedByPayments": 30,
      |      "balance": 200,
      |      "dueDate": "2017-07-22"
      |    }
      |  ],
      |  "_embedded": {
      |    "earlierYearUpdate": {
      |      "amount": 700,
      |      "clearedByCredits": 200,
      |      "clearedByPayments": 300,
      |      "balance": 200,
      |      "dueDate": "2017-04-22"
      |    },
      |    "rtiCharges": [
      |      {
      |        "taxMonth": {
      |          "month": 7,
      |          "firstDay": "2017-10-06",
      |          "lastDay": "2017-11-05"
      |        },
      |        "amount": 1200,
      |        "clearedByCredits": 0,
      |        "clearedByPayments": 0,
      |        "balance": 1200,
      |        "dueDate": "2017-11-22",
      |        "isSpecified": false,
      |        "_links": {
      |          "self": {
      |            "href": "$apiBaseUrl/organisations/paye/${empRef.taxOfficeNumber}/${empRef.taxOfficeReference}/statements/2017-18/7"
      |          }
      |        }
      |      },
      |      {
      |        "taxMonth": {
      |          "month": 3,
      |          "firstDay": "2017-06-06",
      |          "lastDay": "2017-07-05"
      |        },
      |        "amount": 700,
      |        "clearedByCredits": 200,
      |        "clearedByPayments": 300,
      |        "balance": 200,
      |        "dueDate": "2017-07-22",
      |        "isSpecified": true,
      |        "_links": {
      |          "self": {
      |            "href": "$apiBaseUrl/organisations/paye/${empRef.taxOfficeNumber}/${empRef.taxOfficeReference}/statements/2017-18/3"
      |          }
      |        }
      |      }
      |    ]
      |  },
      |  "_links": {
      |    "empRefs": {
      |      "href": "$apiBaseUrl/organisations/paye/"
      |    },
      |    "statements": {
      |      "href": "$apiBaseUrl/organisations/paye/${empRef.taxOfficeNumber}/${empRef.taxOfficeReference}/statements"
      |    },
      |    "self": {
      |      "href": "$apiBaseUrl/organisations/paye/${empRef.taxOfficeNumber}/${empRef.taxOfficeReference}/statements/2017-18"
      |    },
      |    "next": {
      |      "href": "$apiBaseUrl/organisations/paye/${empRef.taxOfficeNumber}/${empRef.taxOfficeReference}/statements/2018-19"
      |    },
      |    "previous": {
      |      "href": "$apiBaseUrl/organisations/paye/${empRef.taxOfficeNumber}/${empRef.taxOfficeReference}/statements/2016-17"
      |    }
      |  }
      |}
     """.stripMargin
  )

  def authorisedEnrolmentJson(empRef: EmpRef): String = {
    s"""
       |{
       |  "authorisedEnrolments": [
       |    {
       |      "key": "IR-PAYE",
       |      "identifiers": [
       |        {
       |          "key": "TaxOfficeNumber",
       |          "value": "${empRef.taxOfficeNumber}"
       |        },
       |        {
       |          "key": "TaxOfficeReference",
       |          "value": "${empRef.taxOfficeReference}"
       |        }],
       |      "state": "Activated",
       |      "confidenceLevel": 0,
       |      "delegatedAuthRule": "epaye-auth",
       |      "enrolment": "IR-PAYE"
       |    }
       |  ]
       |}
      """.stripMargin
  }

}
