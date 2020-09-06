package xyz.sangcomz.stickytimelineview.callback

import xyz.sangcomz.stickytimelineview.model.SectionInfo

interface SectionCallback {
    /**
     * To check if section is
     */
    fun isSection(position: Int): Boolean

    /**
     * Functions that return a section header in a section
     */
    fun getSectionHeader(position: Int): SectionInfo?
}