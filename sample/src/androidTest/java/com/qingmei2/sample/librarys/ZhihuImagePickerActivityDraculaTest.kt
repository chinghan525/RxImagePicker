package com.qingmei2.sample.librarys

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qingmei2.rximagepicker_extension.MimeType
import com.qingmei2.rximagepicker_extension.entity.SelectionSpec
import com.qingmei2.rximagepicker_extension_zhihu.ZhihuConfigurationBuilder
import com.qingmei2.rximagepicker_extension_zhihu.ui.ZhihuImagePickerActivity
import com.qingmei2.sample.R
import com.qingmei2.sample.ext.*
import org.hamcrest.Matchers.allOf
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ZhihuImagePickerActivityDraculaTest {

    @Rule
    @JvmField
    val grantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    @Rule
    @JvmField
    val tasksActivityTestRule =
            object : IntentsTestRule<ZhihuImagePickerActivity>(ZhihuImagePickerActivity::class.java) {

                override fun beforeActivityLaunched() {
                    super.beforeActivityLaunched()

                    // Inject the ICustomPickerConfiguration
                    SelectionSpec.instance = ZhihuConfigurationBuilder(MimeType.ofAll(), false)
                            .maxSelectable(9)
                            .countable(true)
                            .spanCount(4)
                            .theme(R.style.Zhihu_Dracula)
                            .build()
                }
            }

    @Test
    fun testSingleTapAndJumpPreviewActivity() {

        checkRecyclerViewExist()

        onView(withId(R.id.recyclerview))
                .clickRecyclerChildWithId(1, R.id.media_thumbnail)

        checkIntendingToPreviewActivity()
    }

    @Test
    fun testSingleCheckAndJumpPreviewActivity() {

        onView(ViewMatchers.withId(R.id.recyclerview))
                .clickRecyclerChildWithId(1, R.id.check_view)

        isCompletelyDisplayed()

        checkRecyclerViewExist()

        onView(withId(R.id.button_preview))
                .perform(click())

        checkIntendingToPreviewActivity()
    }

    @Test
    fun testMultiCheckAndJumpPreviewActivity() {

        onView(withId(R.id.recyclerview))
                .clickRecyclerChildWithId(0, R.id.check_view)
                .clickRecyclerChildWithId(1, R.id.check_view)
                .clickRecyclerChildWithId(2, R.id.check_view)

        checkRecyclerViewExist()

        onView(withId(R.id.button_preview))
                .perform(click())

        checkIntendingToPreviewActivity()
    }

    @Test
    fun testNoImagesSelected_ClickApply() {

        checkRecyclerViewExist()

        onView(withId(R.id.button_apply))
                .perform(click())

        checkRecyclerViewExist()
    }

    @Test
    fun testNoImagesSelected_CheckAndCancelThenApply() {

        checkRecyclerViewExist()

        // select image
        onView(withId(R.id.recyclerview))
                .perform(actionOnItemAtPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(
                        1, clickRecyclerChildWithId(R.id.check_view)
                ))

        // cancel select
        onView(withId(R.id.recyclerview))
                .perform(actionOnItemAtPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(
                        1, clickRecyclerChildWithId(R.id.check_view)
                ))

        onView(withId(R.id.button_apply))
                .perform(click())

        checkRecyclerViewExist()
    }

    @Test
    fun testSingleImagesSelectedAndClickApply() {

        checkRecyclerViewExist()

        // select image
        onView(withId(R.id.recyclerview))
                .clickRecyclerChildWithId(1, R.id.check_view)

        onView(withId(R.id.button_apply))
                .perform(ViewActions.click())

        Assert.assertTrue(tasksActivityTestRule.isFinished())
    }

    @Test
    fun testMultiImagesSelectedAndClickApply() {

        checkRecyclerViewExist()

        // select image
        onView(withId(R.id.recyclerview))
                .clickRecyclerChildWithId(0, R.id.check_view)
                .clickRecyclerChildWithId(1, R.id.check_view)
                .clickRecyclerChildWithId(2, R.id.check_view)
                .clickRecyclerChildWithId(3, R.id.check_view)
                .clickRecyclerChildWithId(4, R.id.check_view)
                .clickRecyclerChildWithId(5, R.id.check_view)
                .clickRecyclerChildWithId(6, R.id.check_view)

        onView(withId(R.id.button_apply))
                .perform(ViewActions.click())

        Assert.assertTrue(tasksActivityTestRule.isFinished())
    }

    private fun checkRecyclerViewExist() =
            onViewId(R.id.recyclerview).checkIsExist()


    private fun checkRecyclerViewNotExist() =
            onViewId(R.id.recyclerview).checkNotExist()


    private fun checkIntendingToPreviewActivity() {

        intending(allOf(
                toPackage(PACKAGE_NAME_PREVIEW_ACTIVITY),
                hasComponent(hasShortClassName(SHORT_NAME_PREVIEW_ACTIVITY))
        ))

        checkRecyclerViewNotExist()
    }

    companion object {

        private const val PACKAGE_NAME_PREVIEW_ACTIVITY = "com.qingmei2.rximagepicker_extension"
        private const val SHORT_NAME_PREVIEW_ACTIVITY = ".ui.SelectedPreviewActivity"
    }
}