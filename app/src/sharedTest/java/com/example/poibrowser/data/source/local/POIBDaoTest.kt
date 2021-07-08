package com.example.poibrowser.data.source.local

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.poibrowser.data.model.FourSquareModel
import com.example.poibrowser.data.model.FourSquareModelContact
import com.example.poibrowser.data.model.FourSquareModelLocation
import com.google.android.gms.maps.model.LatLng
import org.hamcrest.CoreMatchers.`is`
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import com.google.common.truth.Truth.assertThat
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author Tomislav Curis
 */

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class POIBDaoTest {
    private lateinit var database: POIBDatabase
    private lateinit var poibDAO: POIBDao

    companion object {
        val dummyVenues = arrayListOf<FourSquareModel>(
            FourSquareModel("1234", "Venue1", FourSquareModelContact("phone", "Twitter"),
                FourSquareModelLocation("52.500337048569975", "13.425081651523898", emptyList()),"someUrl.com"),
            FourSquareModel("4321", "Venue2", FourSquareModelContact("phone", "Twitter"),
                FourSquareModelLocation("52.500337048569975", "13.425081651523898", emptyList()),"someUrl.com"),
            FourSquareModel("436754", "Venue3", FourSquareModelContact("phone", "Twitter"),
                FourSquareModelLocation("52.500337048569975", "13.425081651523898", emptyList()),"someUrl.com")
        )

        val dummyLatLng = LatLng(52.500337048569975, 13.425081651523898)
    }

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDB () {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), POIBDatabase::class.java).build()
        poibDAO = database.getPOIBDao()
    }

    @After
    fun closeDB() = database.close()

    private var venues = listOf(dummyVenues[0], dummyVenues[1])

    @Test
    fun insertVenuesAndGet() = runBlockingTest {
        // Insert a venue
        poibDAO.saveVenues(venues)

        // retrieve list
        val venuesTemp = poibDAO.getVenues("52.49115428200564","52.50951981513431",  "13.4173572248371", "13.432806078210698")

        assertThat(venuesTemp[0].id).isEqualTo(venues[0].id)
        assertThat(venuesTemp[0].name).isEqualTo(venues[0].name)
        assertThat(venuesTemp[1].id).isEqualTo(venues[1].id)
        assertThat(venuesTemp[1].name).isEqualTo(venues[1].name)
    }


    @Test
    fun insertVenueAndGet() = runBlockingTest {
        // Insert a venue
        poibDAO.saveVenue(venues[0])

        // retrieve list
        val venueTemp = poibDAO.getVenue(venues[0].id)

        assertThat(venueTemp.id).isEqualTo(venues[0].id)
        assertThat(venueTemp.name).isEqualTo(venues[0].name)
        assertThat(venueTemp.canonicalUrl).isEqualTo(venues[0].canonicalUrl)
    }

    @Test
    fun insertVenuesReplacesOnConflict() = runBlockingTest {
        // Insert venues
        poibDAO.saveVenues(venues)

        // When a venues with the same ids are inserted
        val newVenues =
            listOf(
                FourSquareModel("1234", "Venue1", FourSquareModelContact("phone", "gfgdfg"),
                    FourSquareModelLocation("52.500337048569975", "13.425081651523898", emptyList()),"gfdgd.com"),
                FourSquareModel("4321", "Venue2", FourSquareModelContact("phone", "gdfgd"),
                    FourSquareModelLocation("52.500337048569975", "13.425081651523898", emptyList()),"gfdgd.com"))

        poibDAO.saveVenues(newVenues)

        // retrieve list
        val venuesTemp = poibDAO.getVenues("52.49115428200564","52.50951981513431",  "13.4173572248371", "13.432806078210698")


        assertThat(venuesTemp[0].id, `is` (venues[0].id))
        assertThat(venuesTemp[0].name, `is` (venues[0].name))
        assertThat(venuesTemp[1].id, `is` (venues[1].id))
        assertThat(venuesTemp[1].name, `is` (venues[1].name))
    }

    @Test
    fun deleteVenues() = runBlockingTest {
        // Insert venues
        poibDAO.saveVenues(venues)

        // Delete venues
        poibDAO.deleteVenues()

        // retrieve venues
        val venuesTemp = poibDAO.getVenues("52.49115428200564","52.50951981513431",  "13.4173572248371", "13.432806078210698")

        assertThat(venuesTemp.isEmpty(), CoreMatchers.`is`(true))
    }

}