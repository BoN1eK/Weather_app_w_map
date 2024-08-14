package com.example.myfirstapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

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
