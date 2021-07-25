package com.trueaccord


import com.trueaccord.DebtPaymentDetail._
import io.circe.syntax.EncoderOps
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DebtStatusCalc extends App {

  val debtsEither = ReadJsonFromUrl.urlInto[List[Debts]](Urls.debts)
  val ppsEither = ReadJsonFromUrl.urlInto[List[PaymentPlans]](Urls.paymentPlans)
  val paymentsEither = ReadJsonFromUrl.urlInto[List[Payments]](Urls.payments)

/*
  find days between two date, for example between 2020-01-01 and 2020-08-08 is 220 days
 */
  def diffDays(psDate: String, pDate: String): Int = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val oldDate = LocalDate.parse(psDate, formatter)
    val newDate = LocalDate.parse(pDate, formatter)
    (newDate.toEpochDay - oldDate.toEpochDay).toInt
  }

  def addDays(strDate: String, dayToAdd: Int): String = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val lastPaymentDay = LocalDate.parse(strDate, formatter)
    DateTimeFormatter.ofPattern("yyyy-MM-dd").format(lastPaymentDay.plusDays(dayToAdd))
  }

   def calcDebtStatus(data: Data): List[DebtStatusReport] = {
    data.debts.map { debt =>
      val isInpps = data.pps.map { x => x.debtId }.exists(x => x == debt.id)
      val remainings = isInpps match {
        case true => {
          val paymentPlan = data.pps.find(x => x.debtId == debt.id).get
          val totalPaymentForEachDebt = data.payments.filter(x => x.paymentPlanId == paymentPlan.id)
          val totalAmount = totalPaymentForEachDebt.map(x => x.amount).sum
          val remainingAmount = (math rint (paymentPlan.amountToPay - totalAmount) * 100) / 100
          val lastPaymentDate = totalPaymentForEachDebt.map(x => x.date).sorted.reverse.head
          val paymentFreq = paymentPlan.installmentFrequency match {
            case "WEEKLY" => 7
            case _ => 14
          }
          val dateDiff = diffDays(paymentPlan.startDate, lastPaymentDate) % paymentFreq
          val nextPaymentDueDate = (remainingAmount, dateDiff, lastPaymentDate) match {
            case (r, _, _) if r <= 0.0 => None
            case (_, diff, lp) if diff == 0 => Some(addDays(lp, paymentFreq))
            case (_, diff, lp) => Some(addDays(lp, paymentFreq - diff))
          }

          (remainingAmount, nextPaymentDueDate)
        }
        case false => (debt.amount, None)
      }
      DebtStatusReport(amount = debt.amount, id = debt.id, isInpps, remainings._1, remainings._2)
    }
  }

  val debtStatusReport = for {
    debts <- debtsEither
    pps <- ppsEither
    payments <- paymentsEither
  } yield calcDebtStatus(Data(debts, pps, payments))

  debtStatusReport.fold(x => println(x.getMessage), x => x.map(d => println(d.asJson.toString().replaceAll("\\n", ""))))

}

