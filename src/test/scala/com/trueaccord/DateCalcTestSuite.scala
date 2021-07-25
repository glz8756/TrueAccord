package com.trueaccord

import com.trueaccord.DebtStatusCalc.diffDays
import org.scalatest.funsuite.AnyFunSuite

class DateCalcTestSuite extends AnyFunSuite{
  test("Should be 220 days between 2020-01-01 and 2020-08-08") {
    assert(diffDays("2020-01-01", "2020-08-08") == 220)
  }
}
