package com.example.myfirstapp

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testUIElementsAreDisplayed() {
        onView(withId(R.id.editTextCity)).check(matches(isDisplayed()))
        onView(withId(R.id.buttonSubmit)).check(matches(isDisplayed()))
        onView(withId(R.id.buttonLoc)).check(matches(isDisplayed()))
        onView(withId(R.id.textViewResult)).check(matches(isDisplayed()))
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

}
