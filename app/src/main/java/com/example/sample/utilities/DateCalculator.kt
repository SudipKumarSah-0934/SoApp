package v2.ltd.nc_retailer.utilities

import v2.ltd.nc_retailer.utilities.DateCalculator
import java.util.*

class DateCalculator
/**
 * Construct DateCalculator Class
 * @param year
 * @param month
 * @param day
 * @param totalDay
 */(
    /**
     *
     * @return years
     */
    val year: Int,
    /**
     *
     * @return months
     */
    val month: Int,
    /**
     *
     * @return days
     */
    val day: Int,
    /**
     *
     * @return total days
     */
    val totalDay: Int
) {

    companion object {
        /**
         * Calculate days for given month and year
         * @param tMonth
         * @param tYear
         * @return days of a given month of given year
         */
        fun MonthsToDays(tMonth: Int, tYear: Int): Int {
            return if (tMonth == 1 || tMonth == 3 || tMonth == 5 || tMonth == 7 || tMonth == 8 || tMonth == 10 || tMonth == 12) {
                31
            } else if (tMonth == 2) {
                if (tYear % 4 == 0) {
                    29
                } else {
                    28
                }
            } else {
                30
            }
        }

        /**
         * Calculate Age in years, months and days and total days
         * @param startDate
         * @param endDate
         * @return DateCalculator
         */
        @JvmStatic
        fun calculateAge(startDate: Calendar, endDate: Calendar): DateCalculator {
            val mycal1 = startDate.clone() as Calendar
            val mycal2 = endDate.clone() as Calendar
            val d1 = mycal1.timeInMillis
            val d2 = mycal2.timeInMillis

//        if (d1 > d2) {
//            mycal1 = (Calendar) endDate.clone();
//            mycal2 = (Calendar) startDate.clone();
//        }
            val mDay = mycal1[Calendar.DAY_OF_MONTH]
            val mMonth = mycal1[Calendar.MONTH]
            val mYear = mycal1[Calendar.YEAR]
            val tDay = mycal2[Calendar.DAY_OF_MONTH]
            val tMonth = mycal2[Calendar.MONTH]
            val tYear = mycal2[Calendar.YEAR]
            val totalDays = gregorianDays(tYear, tMonth, tDay) - gregorianDays(mYear, mMonth, mDay)
            var mYearDiff = tYear - mYear
            var mMonDiff = tMonth - mMonth
            if (mMonDiff < 0) {
                mYearDiff = mYearDiff - 1
                mMonDiff = mMonDiff + 12
            }
            var mDayDiff = tDay - mDay
            if (mDayDiff < 0) {
                if (mMonDiff > 0) {
                    mMonDiff = mMonDiff - 1
                    mDayDiff = mDayDiff + MonthsToDays(tMonth - 1, tYear)
                } else {
                    mYearDiff = mYearDiff - 1
                    mMonDiff = 11
                    mDayDiff = mDayDiff + MonthsToDays(tMonth - 1, tYear)
                }
            }
            //String age = "Age: " + mYearDiff + " Years " + mMonDiff + " Months " + mDayDiff + " Days";
            //System.out.println(age);
            return DateCalculator(mYearDiff, mMonDiff, mDayDiff, totalDays)
        }

        private fun gregorianDays(year: Int, month: Int, day: Int): Int {
            var year = year
            var month = month
            month = (month + 9) % 12
            year = year - month / 10
            return 365 * year + year / 4 - year / 100 + year / 400 + (month * 306 + 5) / 10 + (day - 1)
        }
    }
}