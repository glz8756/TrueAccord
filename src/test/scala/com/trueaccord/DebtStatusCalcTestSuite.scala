package com.trueaccord

import com.trueaccord.DebtPaymentDetail.{Data, DebtStatusReport, Debts, PaymentPlans, Payments}
import com.trueaccord.DebtStatusCalc.calcDebtStatus
import org.scalatest.funsuite.AnyFunSuite
import io.circe.parser.decode

class DebtStatusCalcTestSuite extends AnyFunSuite {
  val rawBebts =
    """[
      |  {
      |    "amount": 123.46,
      |    "id": 0
      |  },
      |  {
      |    "amount": 100,
      |    "id": 1
      |  },
      |  {
      |    "amount": 4920.34,
      |    "id": 2
      |  },
      |  {
      |    "amount": 12938,
      |    "id": 3
      |  },
      |  {
      |    "amount": 9238.02,
      |    "id": 4
      |  },
      |  {
      |    "amount": 100.00,
      |    "id": 5
      |  },
      |  {
      |    "amount": 50.00,
      |    "id": 6
      |  }
      |]""".stripMargin

  val rawPps =
    """[
      |  {
      |    "amount_to_pay": 102.5,
      |    "debt_id": 0,
      |    "id": 0,
      |    "installment_amount": 51.25,
      |    "installment_frequency": "WEEKLY",
      |    "start_date": "2020-09-28"
      |  },
      |  {
      |    "amount_to_pay": 100,
      |    "debt_id": 1,
      |    "id": 1,
      |    "installment_amount": 25,
      |    "installment_frequency": "WEEKLY",
      |    "start_date": "2020-08-01"
      |  },
      |  {
      |    "amount_to_pay": 4920.34,
      |    "debt_id": 2,
      |    "id": 2,
      |    "installment_amount": 1230.085,
      |    "installment_frequency": "BI_WEEKLY",
      |    "start_date": "2020-01-01"
      |  },
      |  {
      |    "amount_to_pay": 4312.67,
      |    "debt_id": 3,
      |    "id": 3,
      |    "installment_amount": 1230.085,
      |    "installment_frequency": "WEEKLY",
      |    "start_date": "2020-08-01"
      |  },
      |  {
      |    "amount_to_pay": 90.00,
      |    "debt_id": 5,
      |    "id": 5,
      |    "installment_amount": 30,
      |    "installment_frequency": "WEEKLY",
      |    "start_date": "2020-08-01"
      |  },
      |  {
      |    "amount_to_pay": 45.00,
      |    "debt_id": 6,
      |    "id": 6,
      |    "installment_amount": 15,
      |    "installment_frequency": "BI_WEEKLY",
      |    "start_date": "2020-08-01"
      |  }
      |]""".stripMargin

  val rawPlayments =
    """[
      |  {
      |    "amount": 51.25,
      |    "date": "2020-09-29",
      |    "payment_plan_id": 0
      |  },
      |  {
      |    "amount": 51.25,
      |    "date": "2020-10-29",
      |    "payment_plan_id": 0
      |  },
      |  {
      |    "amount": 25,
      |    "date": "2020-08-08",
      |    "payment_plan_id": 1
      |  },
      |  {
      |    "amount": 25,
      |    "date": "2020-08-08",
      |    "payment_plan_id": 1
      |  },
      |  {
      |    "amount": 4312.67,
      |    "date": "2020-08-08",
      |    "payment_plan_id": 2
      |  },
      |  {
      |    "amount": 1230.085,
      |    "date": "2020-08-01",
      |    "payment_plan_id": 3
      |  },
      |  {
      |    "amount": 1230.085,
      |    "date": "2020-08-08",
      |    "payment_plan_id": 3
      |  },
      |  {
      |    "amount": 1230.085,
      |    "date": "2020-08-15",
      |    "payment_plan_id": 3
      |  },
      |  {
      |    "amount": 90.00,
      |    "date": "2020-08-05",
      |    "payment_plan_id": 5
      |  },
      |  {
      |    "amount": 30.00,
      |    "date": "2020-08-17",
      |    "payment_plan_id": 6
      |  }
      |]""".stripMargin

  val debts = decode[List[Debts]](rawBebts).getOrElse(List())
  val pps = decode[List[PaymentPlans]](rawPps).getOrElse(List())
  val payments = decode[List[Payments]](rawPlayments).getOrElse(List())
  val data = Data(debts, pps, payments)
  val report: List[DebtStatusReport] = calcDebtStatus(data: Data)

  test("A Debt has been paid off so the next_payment_due_date should be None") {
    assert(report(0).nextPaymentDueDate == None)
  }

  test("A Debt has no payment plan so the next_payment_due_date should be None") {
    assert(report(4).nextPaymentDueDate == None)
  }

  test("a debt which has BI_WEEKLY installment so the next_payment_due_date should be 2020-08-12") {
    assert(report(2).nextPaymentDueDate == Some("2020-08-12"))
  }

  test("a debt which has WEEKLY installment so the next_payment_due_date should be 2020-08-15") {
    assert(report(1).nextPaymentDueDate == Some("2020-08-15"))
  }

  test("A Debt has been paid off so the remaining_amount should be 0.0") {
    assert(report(0).RemainingAmount == 0.0)
  }

  test("the remaining_amount  should be 607.67") {
    assert(report(2).RemainingAmount == 607.67)
  }



}
