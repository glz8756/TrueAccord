package com.trueaccord

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

object DebtPaymentDetail {

  @ConfiguredJsonCodec
  final case class Debts
  (
    amount: Double,
    id: Int
  )

  @ConfiguredJsonCodec
  final case class PaymentPlans
  (
    amountToPay: Double,
    debtId: Int,
    id: Int,
    installmentAmount: Double,
    installmentFrequency: String,
    startDate: String
  )

  @ConfiguredJsonCodec
  final case class Payments
  (
    amount: Double,
    date: String,
    paymentPlanId: Int
  )

  final case class Data
  (
  debts: List[Debts],
  pps: List[PaymentPlans],
  payments: List[Payments]
  )

  @ConfiguredJsonCodec
  final case class DebtStatusReport
  (
    amount: Double,
    id: Int,
    isInPaymentPlan: Boolean,
    RemainingAmount: Double,
    nextPaymentDueDate: Option[String]
  )

  implicit lazy val config: Configuration = Configuration.default.withSnakeCaseMemberNames

}
