package com.trueaccord

import com.trueaccord.DebtPaymentDetail._
import io.circe.{Json, ParsingFailure}
import io.circe.parser._
import org.scalatest.funsuite.AnyFunSuite

class DebtPaymentDetailTestSuite extends AnyFunSuite {
  val rawDebt =
    """ {
      |    "amount": 123.46,
      |    "id": 0
      |  }""".stripMargin

  val rawPps =
    """{
      |    "amount_to_pay": 102.5,
      |    "debt_id": 0,
      |    "id": 0,
      |    "installment_amount": 51.25,
      |    "installment_frequency": "WEEKLY",
      |    "start_date": "2020-09-28"
      |  }""".stripMargin

  val rawPayment =
    """{
      |    "amount": 51.25,
      |    "date": "2020-09-29",
      |    "payment_plan_id": 0
      |  }""".stripMargin

  val jsonDebt: Either[ParsingFailure, Json] = parse(rawDebt)
  test("A Debt object should be successfully created from valid json.") {
    assume(jsonDebt.isRight, "Assume that circe can successfully parse well formed json.")
    val result = jsonDebt.flatMap(_.as[Debts])
    assert(result.isRight)
  }

  val jsonPps: Either[ParsingFailure, Json] = parse(rawPps)
  test("A PaymentPlans object should be successfully created from valid json.") {
    assume(jsonPps.isRight, "Assume that circe can successfully parse well formed json.")
    val result = jsonPps.flatMap(_.as[PaymentPlans])
    assert(result.isRight)
  }

  val jsonPayment: Either[ParsingFailure, Json] = parse(rawPayment)
  test("A Payments object should be successfully created from valid json.") {
    assume(jsonPayment.isRight, "Assume that circe can successfully parse well formed json.")
    val result = jsonPayment.flatMap(_.as[Payments])
    assert(result.isRight)
  }
}
