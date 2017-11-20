package xyz.sangcomz.stickytimelineview.ext

import android.content.Context

/**
 * Created by seokwon.jeong on 16/11/2017.
 */
fun Int.DP(context: Context): Float = (this * context.resources.displayMetrics.density)

fun Float.DP(context: Context): Float = (this * context.resources.displayMetrics.density)