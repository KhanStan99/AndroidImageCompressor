package `in`.trentweet.imagecompressor.utilities

import android.util.Log

object Logger {
    private const val isLogEnabled = true
    fun error(tag: String?, msg: String?) {
        if (isLogEnabled) Log.e(tag, msg)
    }

    fun info(tag: String?, msg: String?) {
        if (isLogEnabled) Log.i(tag, msg)
    }

    fun verbose(tag: String?, msg: String?) {
        if (isLogEnabled) Log.v(tag, msg)
    }

    fun debug(tag: String?, msg: String?) {
        if (isLogEnabled) Log.d(tag, msg)
    }

    fun printMessage(msg: String?) {
        if (isLogEnabled) println(msg)
    }
}