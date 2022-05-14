package com.blog.test

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.blog.a.R
import com.blog.demo40.EspressoUiTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class EspressoUiTest {

    @get:Rule var activityScenarioRule = activityScenarioRule<EspressoUiTest>()

    @Test
    fun test1() {
        onView(withId(R.id.btn_test1)).perform(click())
        onView(withId(R.id.tv_show_test1)).check(matches(withText("espresso")))
    }
}