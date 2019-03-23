package xyz.sangcomz.stickytimelineview.model

import android.graphics.drawable.Drawable

/**
 * Created by seokwon.jeong on 16/11/2017.
 */
data class SectionInfo(
    val title: String,
    val subTitle: String? = null,
    val dotDrawable: Drawable? = null
)