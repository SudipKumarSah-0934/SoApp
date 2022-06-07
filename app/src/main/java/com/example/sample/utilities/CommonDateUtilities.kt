package v2.ltd.nc_retailer.utilities

import android.content.Context
import android.graphics.Color
import v2.ltd.nc_retailer.utilities.DateCalculator.Companion.calculateAge

import android.text.format.DateUtils
import android.text.SpannableStringBuilder
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import java.lang.Exception
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CommonDateUtilities(var context: Context) {
    var MILLS_IN_YEAR = 1000L * 60 * 60 * 24 * 365
    fun getYearDifference(date: String?): Int {
        var deff = 0
        val dfff = 0.0
        try {
            val dateFormatprev = SimpleDateFormat("yyyy-MM-dd")
            var d: Date? = null
            try {
                d = dateFormatprev.parse(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val cal = Calendar.getInstance()
            cal.time = d
            // int  yer = (cal.get(Calendar.YEAR));
            val caal = Calendar.getInstance()
            // int  pyer = (caal.get(Calendar.YEAR));
            val dateCaculator = calculateAge(cal, caal)
            Log.d("getYearDifference", dateCaculator.year.toString())
            //            timedefference=  String.format("%02d : %02d ",
//                    TimeUnit.MILLISECONDS.to(Math.abs(mills)),
//                    TimeUnit.MILLISECONDS.toSeconds(Math.abs(mills)) -
//                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes( Math.abs(mills)))
//            );
//
//            deff = pyer-yer;
            deff = dateCaculator.year
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return deff
    }

    fun getYearDifferenceforshow(date: String?): Int {
        //    String dateFromDB = "10/02/2002";
        var deff = 0
        try {
            val dateFormatprev = SimpleDateFormat("yyyy-MM-dd")
            var d: Date? = null
            try {
                d = dateFormatprev.parse(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val cal = Calendar.getInstance()
            cal.time = d
            val yer = cal[Calendar.YEAR]
            val caal = Calendar.getInstance()
            val pyer = caal[Calendar.YEAR]
            deff = pyer - yer
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return deff
    }

    fun getTimeDifference(Starttime: String?, Endtime: String?): String {
        //  String Starttime = "16:01:02";
        // String Endtime = "16:03:44";
        val deffhr = 0
        val deffmn = 0
        val deffsc = 0
        var timedefference = ""
        try {
            val dateFormatprev = SimpleDateFormat("HH:mm:ss")
            var d: Date? = null
            try {
                d = dateFormatprev.parse(Starttime)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            //SimpleDateFormat dateFormat = new SimpleDateFormat("HH mm ss");
            var endt: Date? = null
            try {
                endt = dateFormatprev.parse(Endtime)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val cal = Calendar.getInstance()
            cal.time = d
            val caal = Calendar.getInstance()
            caal.time = endt
            val d1 = cal.time
            val d2 = caal.time
            val mills = d1.time - d2.time
            timedefference = String.format(
                "%02d : %02d ",
                TimeUnit.MILLISECONDS.toMinutes(Math.abs(mills)),
                TimeUnit.MILLISECONDS.toSeconds(Math.abs(mills)) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Math.abs(mills)))
            )
            Log.d("gfdfsgdfgd", timedefference)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("gfdfsgdfgd", e.message.toString())
        }
        return timedefference
    }

    fun getSessionTimeDiff(Starttime: String?, Endtime: String?): String {
        //  String Starttime = "16:01:02";
        // String Endtime = "16:03:44";
        val deffhr = 0
        val deffmn = 0
        val deffsc = 0
        var timedefference = ""
        try {
            val dateFormatprev = SimpleDateFormat("yyyy:MM:dd:HH:mm:ss")
            var d: Date? = null
            try {
                d = dateFormatprev.parse(Starttime)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            //SimpleDateFormat dateFormat = new SimpleDateFormat("HH mm ss");
            var endt: Date? = null
            try {
                endt = dateFormatprev.parse(Endtime)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val cal = Calendar.getInstance()
            cal.time = d
            val caal = Calendar.getInstance()
            caal.time = endt
            val d1 = cal.time
            val d2 = caal.time
            val mills = d1.time - d2.time
            timedefference = String.format("%d", TimeUnit.MILLISECONDS.toMinutes(Math.abs(mills)))
            Log.d("gfdfsgdfgd", timedefference)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("gfdfsgdfgd", e.message.toString())
        }
        return timedefference
    }

    val currentDate: Int
        get() {
            val caal = Calendar.getInstance()
            return caal[Calendar.DATE]
        }

    fun getdatetime(): String {
        val newdate = ""
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdt = df.format(Date(System.currentTimeMillis()))
        Log.d("getdatetie", sdt)
        return sdt
    }

    val contactDate: String
        get() {
            val newdate = ""
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            val sdt = df.format(Date(System.currentTimeMillis()))
            Log.d("getdatetie", sdt)
            return sdt
        }
    val hourMintSecond: String
        get() {
            val newdate = ""
            val df: DateFormat = SimpleDateFormat("HH:mm:ss")
            val sdt = df.format(Date(System.currentTimeMillis()))
            Log.d("getdatetie", sdt)
            return sdt
        }

    fun calculatelastSeen(createdAt: String?): String {
        var ago: CharSequence = ""
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        try {
            val time = sdf.parse(createdAt).time
            val now = System.currentTimeMillis()
            ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ago.toString()
    }

    fun loadClorString(name: String, Value: String?): SpannableStringBuilder {
        val redSpannable = SpannableString(name)
        redSpannable.setSpan(ForegroundColorSpan(Color.parseColor("#3170B0")), 0, name.length, 0)
        val builder = SpannableStringBuilder()
        return builder.append(" ").append(redSpannable).append(" - ").append(Value)
    }

    fun getHumanTimeFormatFromMilliseconds(millisecondS: String?): String {
        var message = ""
        val milliseconds = java.lang.Long.valueOf(millisecondS)
        message = if (milliseconds >= 1000) {
            val seconds = (milliseconds / 1000).toInt() % 60
            val minutes = (milliseconds / (1000 * 60) % 60).toInt()
            val hours = (milliseconds / (1000 * 60 * 60) % 24).toInt()
            val days = (milliseconds / (1000 * 60 * 60 * 24)).toInt()
            if (days == 0 && hours != 0) {
                String.format("%d hours %d minutes %d seconds", hours, minutes, seconds)
            } else if (hours == 0 && minutes != 0) {
                String.format("%d minutes %d seconds", minutes, seconds)
            } else if (days == 0 && hours == 0 && minutes == 0) {
                String.format("%d seconds", seconds)
            } else {
                String.format(
                    "%d days %d hours %d minutes %d seconds",
                    days,
                    hours,
                    minutes,
                    seconds
                )
            }
        } else {
            "Less than a second."
        }
        return message
    }

    companion object {
        fun formatDate(fromFormat: String?, toFormat: String?, dateToFormat: String?): String {
            val inFormat = SimpleDateFormat(fromFormat)
            var date: Date? = null
            try {
                date = inFormat.parse(dateToFormat)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val outFormat = SimpleDateFormat(toFormat)
            return outFormat.format(date)
        }
    }
}