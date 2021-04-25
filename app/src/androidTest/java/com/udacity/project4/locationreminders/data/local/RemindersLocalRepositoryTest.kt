package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    //    TODO: Add testing implementation to the RemindersLocalRepository.kt
    private lateinit var database: RemindersDatabase
    private lateinit var remindersDAO: RemindersDao
    private lateinit var repository: RemindersLocalRepository

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        remindersDAO = database.reminderDao()
        repository =
            RemindersLocalRepository(
                remindersDAO,
                Dispatchers.Main
            )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun getReminder_reminder_returnsReminder() = mainCoroutineRule.runBlockingTest {
        //GIVEN a new reminder saved in the database
        val reminder = ReminderDTO("title", "description", "location", 0.0, 0.0)
        repository.saveReminder(reminder)

        //WHEN you call get reminder
        val reminderLoaded = repository.getReminder(reminder.id) as Result.Success<ReminderDTO>
        val result = reminderLoaded.data

        //THEN the same reminder is returned
        assertThat(result, Matchers.notNullValue())
        assertThat(result.id, `is`(reminder.id))
        assertThat(result.title, `is`(reminder.title))
        assertThat(result.description, `is`(reminder.description))
        assertThat(result.location, `is`(reminder.location))
        assertThat(result.latitude, `is`(reminder.latitude))
        assertThat(result.longitude, `is`(reminder.longitude))
    }

    @Test
    fun getReminder_invalidReminder_returnsError() = mainCoroutineRule.runBlockingTest {
        //GIVEN a reminder not inserted in the database
        val reminder = ReminderDTO("title", "description", "location", 0.0, 0.0)

        //WHEN you call get reminder
        val result = repository.getReminder(reminder.id) as Result.Error

        //THEN the reminder is not found
        assertThat(result.message, Matchers.notNullValue())
        assertThat(result.message, `is`("Reminder not found!"))
    }

    @Test
    fun getReminders_deleteReminders_returnsEmptyList() = mainCoroutineRule.runBlockingTest {
        //GIVEN two reminders saved in the database
        val reminder1 = ReminderDTO("title1", "description1", "location1", 1.0, 1.0)
        val reminder2 = ReminderDTO("title2", "description2", "location2", 2.0, 2.0)
        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)

        //WHEN you call deleteAllReminders and getReminders
        repository.deleteAllReminders()
        val reminders = repository.getReminders() as Result.Success<List<ReminderDTO>>
        val data = reminders.data

        //Then the result list is empty
        assertThat(data.isEmpty(), `is`(true))
    }
}