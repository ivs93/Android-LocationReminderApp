package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
/**
 * Added this line to be able to run the test on my machine. Please remove if necessary to run the test
 */
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    //TODO: provide testing to the SaveReminderView and its live data objects

    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    var mainCoroutineRule = MainCoroutineRule()
    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var dataSource: FakeDataSource

    @Before
    fun setUp() {
        stopKoin()
        dataSource = FakeDataSource()
        saveReminderViewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), dataSource)

    }

    @Test
    fun showSnackBar_emptyTitle_true() = mainCoroutineRule.runBlockingTest {
        val reminder = ReminderDataItem(
            "", "description", "location", 0.0, 0.0
        )
        val result = saveReminderViewModel.validateEnteredData(reminder)
        MatcherAssert.assertThat(result, CoreMatchers.`is`(false))
        MatcherAssert.assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            CoreMatchers.`is`(R.string.err_enter_title)
        )
    }

    @Test
    fun showSnackBar_nullTitle_true() =
        mainCoroutineRule.runBlockingTest {
            val reminder = ReminderDataItem(
                null, "description", "location", 0.0, 0.0
            )
            val result = saveReminderViewModel.validateEnteredData(reminder)
            MatcherAssert.assertThat(result, CoreMatchers.`is`(false))
            MatcherAssert.assertThat(
                saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
                CoreMatchers.`is`(R.string.err_enter_title)
            )
        }

    @Test
    fun showSnackBar_emptyLocation_true() =
        mainCoroutineRule.runBlockingTest {
            val reminder = ReminderDataItem(
                "title", "description", "", 0.0, 0.0
            )

            val result = saveReminderViewModel.validateEnteredData(reminder)
            MatcherAssert.assertThat(result, CoreMatchers.`is`(false))
            MatcherAssert.assertThat(
                saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
                CoreMatchers.`is`(R.string.err_select_location)
            )
        }

    @Test
    fun showSnackBar_nullLocation_true() =
        mainCoroutineRule.runBlockingTest {
            val reminder = ReminderDataItem(
                "title", "description", null, 0.0, 0.0
            )

            val result = saveReminderViewModel.validateEnteredData(reminder)
            MatcherAssert.assertThat(result, CoreMatchers.`is`(false))
            MatcherAssert.assertThat(
                saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
                CoreMatchers.`is`(R.string.err_select_location)
            )
        }

    @Test
    fun showLoading_saveReminder_TrueAndFalse() = mainCoroutineRule.runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        val reminderDataItem = ReminderDataItem(
            "title", "description", "location", 0.0, 0.0
        )
        saveReminderViewModel.saveReminder(
            reminderDataItem
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )
        mainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
    }
}