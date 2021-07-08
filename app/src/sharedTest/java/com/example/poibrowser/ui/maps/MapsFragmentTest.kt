package com.example.poibrowser.ui.maps

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.poibrowser.R
import com.example.poibrowser.TestApp
import com.example.poibrowser.data.source.FakeRepository
import com.example.poibrowser.data.source.MapRepository
import com.example.poibrowser.data.source.local.POIBDaoTest.Companion.dummyVenues
import com.example.poibrowser.ui.map.MapViewModel
import com.example.poibrowser.ui.map.MapsFragment
import com.example.poibrowser.utils.ViewIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito

/**
 * @author Tomislav Curis
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class MapsFragmentTest : KoinTest{

    // Use a fake repository to be injected
    private lateinit var repository: MapRepository

    private var venues = dummyVenues

    private val viewModel : MapViewModel by inject()

    @Before
    fun initRepo() {

        repository = FakeRepository()

        val application = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApp
        application.injectModule(module {
            single(override = true) { repository }
        })

        // Fill the db
        runBlocking {
            repository.saveVenues(venues)
        }
    }

    @Test
    fun displayMap() {
        // GIVEN - On the home screen
        launchFragment()

        // Wait map to be shown
        idlingResourceWithId(R.id.map)

        onView(withId(R.id.map))
            .check(ViewAssertions.matches(isDisplayed()))
    }

//    @Test /*TODO*/
//    fun displayMapAndVenues() {
//        // GIVEN - On the home screen
//        launchFragment()
//
//        // Wait map to be shown
//        idlingResourceWithId(R.id.map)
//
//        onView(withId(R.id.map))
//            .check(ViewAssertions.matches(isDisplayed()))
//
//        onView(withId(R.drawable.ic_baseline_location_on_24)).perform(click())
//
//        // Wait point to be shown
//        idlingResourceWithId(R.drawable.ic_baseline_location_on_24)
//        onView(withId(R.drawable.ic_baseline_location_on_24))
//            .check(ViewAssertions.matches(isDisplayed()))
//
//
//
//
//    }


    private fun launchFragment() {
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<MapsFragment>(Bundle(), R.style.TestTheme)

        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
    }

    private fun idlingResourceWithId(id: Int){
        val matcher = withId(id)
        val resource = ViewIdlingResource(matcher, isEnabled())
        try {
            IdlingRegistry.getInstance().register(resource)
            onView(matcher).check(ViewAssertions.matches(isEnabled()))

        } finally {
            IdlingRegistry.getInstance().unregister(resource)
        }
    }
}