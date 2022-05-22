package com.blog.test

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.view.View
import android.widget.EditText
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.RootMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.blog.R
import com.blog.demo40.CustomAdapter
import com.blog.demo40.EspressoUiActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class EspressoUiTest {
    // 创建并启动测试中的 Activity，并在每次测试后关闭
    @get:Rule var activityScenarioRule = activityScenarioRule<EspressoUiActivity>()

    var hint = "please input"
    var endHint = "input"

    var activity: Activity? = null

    @Before fun init() { // IntentsTestRule 已废弃，用此方法代替
        Intents.init()
        activityScenarioRule.scenario.onActivity {
            activity = it
        }
    }

    @After fun destroy() {
        Intents.release()
    }

    @Test fun test1() {
        // step: 查找Button，并对该视图点击
        onView(withId(R.id.btn_test1)).perform(click())
        // step: 查找TextView，并对其断言
        onView(withId(R.id.tv_test1)).check(matches(withText("espresso")))
    }

    @Test fun test2() {
        // 找到内容 "item5" 的 item，并点击
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("item5"))).perform(click())
        // 找到对应 TextView，并断言内容更新
        onView(withId(R.id.tv_test2)).check(matches(withText("item5")))
    }

    @Test fun test3() {
        // 找到对应 button，执行点击，->跳转到另一个界面
        onView(withId(R.id.btn_intent)).perform(click())
        // 验证 intent 携带的数据，如 action、extra
        //Intents.intended(hasAction("custom.intent.action.demo40"))
        Intents.intended(allOf(hasAction("custom.intent.action.demo40"),
            hasExtraWithKey("type"),
            hasExtra("tag", "zuo")))
    }

    @Test fun test5() {
        // 模拟启动 EspressoUiActivity2 返回的 result
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK,
            Intent().apply { putExtra("tag", "zuo") })
        // intending 类似于 Mockito.when()
        intending(toPackage("com.blog.demo40.EspressoUiActivity2")).respondWith(result)
        onView(withId(R.id.btn_intent)).perform(click())
        onView(withId(R.id.tv_intent)).check(matches(withText("zuo")))
    }

    @Test fun test6() {
        // 找到 RecyclerView，模拟滑动到 item' index = 4，点击
        onView(withId(R.id.recycler_view))
            .perform(actionOnItemAtPosition<CustomAdapter.ViewHolder>(2, click()))
        // 通过内容找到 middle item, 并断言该 item 被展示出来了
        onView(withText("middle")).check(matches(isDisplayed()))
    }

    @Test fun test7() {
        // 滚动到匹配的数据视图持有者
        onView(withId(R.id.recycler_view)).perform(scrollToHolder(isInTheMiddle()))
        onView(withText("middle")).check(matches(isDisplayed()))
    }

    private fun isInTheMiddle(): Matcher<CustomAdapter.ViewHolder> {
        return object : TypeSafeMatcher<CustomAdapter.ViewHolder>() {
            // 匹配条件，注意这里要唯一，如果多个 item 都满足条件则报错
            override fun matchesSafely(customHolder: CustomAdapter.ViewHolder): Boolean {
                return customHolder.isInTheMiddle // true: 匹配上了
            }
            // 当前生成对象的描述
            override fun describeTo(description: Description) {
                description.appendText("item in the middle")
            }
        }
    }

    @Test fun test8() {
        // 断言 editText' hint equals "please input"
        onView(withId(R.id.et_input)).check(matches(withHint(hint))) // hint:"please input"
        // 断言 editText' hint end with "input"
        onView(withId(R.id.et_input)).check(matches(withHint(endsWith(endHint)))) // endHint:"input"
    }

    private fun withHint(substring: String): Matcher<View> {
        return withHint(`is`(substring)) // Matcher<T> is(T value)
    }

    private fun withHint(stringMatcher: Matcher<String>): Matcher<View> {
        return object : BoundedMatcher<View, EditText>(EditText::class.java) {
            // BoundedMatcher<T, S extends T>
            override fun matchesSafely(et: EditText): Boolean {
                // 定义 EditText 匹配规则
                return null != et.hint && stringMatcher.matches(et.hint.toString())
            }
            // 给生成对象和 stringMatcher 添加描述
            override fun describeTo(description: Description) {
                description.appendText("with hint: ")
                stringMatcher.describeTo(description)
            }
        }
    }

    private fun withHint2(stringMatcher: Matcher<String>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            // BoundedMatcher<T, S extends T>
            override fun matchesSafely(et: View): Boolean {
                // 定义 EditText 匹配规则
                val editText = et as EditText
                return (null != editText.hint) && stringMatcher.matches(editText.hint.toString())
            }
            // 给生成对象和 stringMatcher 添加描述
            override fun describeTo(description: Description) {
                description.appendText("with hint: ")
                stringMatcher.describeTo(description)
            }
        }
    }

    @Test fun test9() {
        val matcherView = withId(R.id.btn_visible)
        // 断言button展示出来了
        onView(matcherView).check(matches(isDisplayed()))
        // 找到button，并点击
        onView(matcherView).perform(click())
        // 断言button没有展示了
        onView(matcherView).check(matches(not(isDisplayed())))
    }

    @Test fun test10() {
        val matcherView = withId(R.id.btn_remove)
        // 断言button展示出来了
        onView(matcherView).check(matches(isDisplayed()))
        onView(matcherView).perform(click())
        // 断言button不存在了
        onView(matcherView).check(doesNotExist())
    }

    @Test fun test11() {
        // 找到 AutoCompleteTextView，模拟输入 Red
        onView(withId(R.id.auto_complete_text_view))
            .perform(typeText("Red"), closeSoftKeyboard())
        // 在 非decorView窗口 找 "Red Sea" item，断言 展示了
        onView(withText("Red Sea"))
            .inRoot(withDecorView(not(`is`(activity?.window?.decorView))))
            .check(matches(isDisplayed()))
    }

    @Test fun test12() {
        // 点击 button, open dialog
        onView(withId(R.id.btn_open_dialog)).perform(click())
        // 找到 item3, 并点击
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("item3"))).perform(click())
        // 找到 textView，断言 text = "item3"
        onView(withId(R.id.tv_show_dialog_item)).check(matches(withText("item3")))
    }

}

