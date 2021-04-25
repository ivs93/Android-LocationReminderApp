package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt

    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun cleanUp() = database.close()

    @Test
    fun getReminderById_reminder_returnsReminder() = runBlockingTest {
        //GIVEN a new reminder saved in the database
        val reminder = ReminderDTO("title", "description", "location", 0.0, 0.0)
        database.reminderDao().saveReminder(reminder)

        //WHEN you call getReminderById
        val result = database.reminderDao().getReminderById(reminder.id)

        //THEN the same reminder is returned
        assertThat<ReminderDTO>(result as ReminderDTO, notNullValue())
        assertThat(result.id, `is`(reminder.id))
        assertThat(result.title, `is`(reminder.title))
        assertThat(result.description, `is`(reminder.description))
        assertThat(result.location, `is`(reminder.location))
        assertThat(result.latitude, `is`(reminder.latitude))
        assertThat(result.longitude, `is`(reminder.longitude))
    }

    @Test
    fun getReminders_deleteAll_returnsEmptyList() = runBlockingTest {
        //GIVEN two reminders saved in the database
        val reminder1 = ReminderDTO("title1", "description1", "location1", 1.0, 1.0)
        val reminder2 = ReminderDTO("title2", "description2", "location2", 2.0, 2.0)
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)

        //WHEN you call deleteAllReminders and getReminders
        database.reminderDao().deleteAllReminders()
        val reminders = database.reminderDao().getReminders()

        //Then the result list is empty
        assertThat(reminders.isEmpty(), `is`(true))
    }

    @Test
    fun getReminderById_invalidId_returnsNull() = runBlockingTest {
        //GIVEN a reminder not inserted in the database
        val reminder = ReminderDTO("title", "description", "location", 0.0, 0.0)

        //WHEN you call getReminderById
        val result = database.reminderDao().getReminderById(reminder.id)

        //THEN the result is null
        assertThat(result, CoreMatchers.nullValue())
    }
}