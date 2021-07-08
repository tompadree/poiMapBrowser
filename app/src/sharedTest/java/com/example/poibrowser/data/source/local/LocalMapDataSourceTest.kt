package com.example.poibrowser.data.source.local


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.poibrowser.data.model.FourSquareModel
import com.example.poibrowser.data.model.FourSquareModelContact
import com.example.poibrowser.data.model.FourSquareModelLocation
import com.google.common.truth.Truth.assertThat
import org.hamcrest.MatcherAssert.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.poibrowser.data.model.Result
import com.example.poibrowser.data.model.Result.Success
import com.example.poibrowser.data.source.local.POIBDaoTest.Companion.dummyLatLng
import com.example.poibrowser.utils.MainCoroutineRule


/**
 * @author Tomislav Curis
 */

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalMapDataSourceTest {

    private lateinit var database: POIBDatabase
    private lateinit var localMapDataSource: LocalMapDataSource


    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDB () {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), POIBDatabase::class.java).build()
        localMapDataSource = LocalMapDataSource(database.getPOIBDao(), Dispatchers.Main)
    }

    @After
    fun closeDB() = database.close()

    private var venues = listOf(POIBDaoTest.dummyVenues[0], POIBDaoTest.dummyVenues[1])

    @Test
    fun insertVenuesAndGet() = runBlockingTest {
        // Insert a venue
        localMapDataSource.saveVenues(venues)

        // retrieve list
        val venuesTemp = localMapDataSource.searchVenues(dummyLatLng, 750.0) as Result.Success

        assertThat(venuesTemp.data[0].id).isEqualTo(venues[0].id)
        assertThat(venuesTemp.data[0].name).isEqualTo(venues[0].name)
        assertThat(venuesTemp.data[1].id).isEqualTo(venues[1].id)
        assertThat(venuesTemp.data[1].name).isEqualTo(venues[1].name)
    }


    @Test
    fun insertVenueAndGet() = runBlockingTest {
        // Insert a venue
        localMapDataSource.saveVenue(venues[0])

        // retrieve list
        val venueTemp = localMapDataSource.searchVenues(dummyLatLng, 750.0) as Result.Success

        assertThat(venueTemp.data[0].id).isEqualTo(venues[0].id)
        assertThat(venueTemp.data[0].name).isEqualTo(venues[0].name)
        assertThat(venueTemp.data[0].canonicalUrl).isEqualTo(venues[0].canonicalUrl)
    }

    @Test
    fun insertVenuesReplacesOnConflict() = runBlockingTest {
        // Insert venues
        localMapDataSource.saveVenues(venues)

        // When a venues with the same ids are inserted
        val newVenues =
            listOf(
                FourSquareModel("1234", "Venue1", FourSquareModelContact("phone", "gfgdfg"),
                    FourSquareModelLocation("52.500337048569975", "13.425081651523898", emptyList()),"gfdgd.com"),
                FourSquareModel("4321", "Venue2", FourSquareModelContact("phone", "gdfgd"),
                    FourSquareModelLocation("52.500337048569975", "13.425081651523898", emptyList()),"gfdgd.com")
            )

        localMapDataSource.saveVenues(newVenues)

        // retrieve list
        val venuesTemp = localMapDataSource.searchVenues(dummyLatLng, 750.0) as Result.Success


        assertThat(venuesTemp.data[0].id, CoreMatchers.`is`(venues[0].id))
        assertThat(venuesTemp.data[0].name, CoreMatchers.`is`(venues[0].name))
        assertThat(venuesTemp.data[1].id, CoreMatchers.`is`(venues[1].id))
        assertThat(venuesTemp.data[1].name, CoreMatchers.`is`(venues[1].name))
    }

    @Test
    fun deleteVenues() = runBlockingTest {
        // Insert venues
        localMapDataSource.saveVenues(venues)

        // Delete venues
        localMapDataSource.deleteVenues()

        // retrieve venues
        val venuesTemp = localMapDataSource.searchVenues(dummyLatLng, 750.0) as Result.Success

        assertThat(venuesTemp.data.isEmpty(), CoreMatchers.`is`(true))
    }
}