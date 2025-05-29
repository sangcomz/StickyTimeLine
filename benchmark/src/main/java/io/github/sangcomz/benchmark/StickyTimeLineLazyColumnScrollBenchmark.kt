package io.github.sangcomz.benchmark

import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StickyTimeLineBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollRow() = benchmarkRule.measureRepeated(
        packageName = "io.github.sangcomz.sample.compose",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()
            startActivityAndWait()
        }
    ) {

        val condition = Until.hasObject(By.res("sticky_timeline_lazy_row"))
        device.wait(condition, 5_000)

        val timelineLazyRow = device.findObject(By.res("sticky_timeline_lazy_row"))
            ?: throw IllegalStateException("Cannot find object with resource ID 'sticky_timeline_lazy_row'")
        timelineLazyRow.setGestureMargin(device.displayWidth / 5)

        repeat(10) {
            timelineLazyRow.swipe(Direction.LEFT, 1f, 4000)
            device.waitForIdle()
        }
    }

    @Test
    fun scrollColumn() = benchmarkRule.measureRepeated(
        packageName = "io.github.sangcomz.sample.compose",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()
            startActivityAndWait()
        }
    ) {

        val condition = Until.hasObject(By.res("sticky_timeline_lazy_column"))
        device.wait(condition, 5_000)

        val timelineLazyColumn = device.findObject(By.res("sticky_timeline_lazy_column"))
            ?: throw IllegalStateException("Cannot find object with resource ID 'sticky_timeline_lazy_column'")
        timelineLazyColumn.setGestureMargin(device.displayWidth / 5)

        repeat(15) {
            timelineLazyColumn.swipe(Direction.UP, 1f, 4000)
            device.waitForIdle()
        }
    }
}