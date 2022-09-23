package com.joeloewi.croissant.benchmark

import androidx.benchmark.macro.ExperimentalBaselineProfilesApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalBaselineProfilesApi
@RunWith(AndroidJUnit4ClassRunner::class)
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() = rule.collectBaselineProfile(
        "com.joeloewi.croissant"
    ) {
        startActivityAndWait()

        device.wait(Until.hasObject(By.textContains("HoYoLAB")), 30_000)
    }
}