package com.example.poibrowser.ui.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.poibrowser.data.source.FakeRepository
import com.example.poibrowser.data.source.local.POIBDaoTest.Companion.dummyVenues
import com.example.poibrowser.di.AppModule
import com.example.poibrowser.di.DataModule
import com.example.poibrowser.di.NetModule
import com.example.poibrowser.di.RepoModule
import com.example.poibrowser.getOrAwaitValue
import com.example.poibrowser.observeForTesting
import com.example.poibrowser.utils.MainCoroutineRule
import com.example.poibrowser.utils.network.InternetConnectionManager
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

/**
 * @author Tomislav Curis
 */

@ExperimentalCoroutinesApi
class MapViewModelTest : KoinTest {

    // What is testing
    private lateinit var mapViewModel: MapViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var repository: FakeRepository

    // Rule for koin injection
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(listOf(AppModule, DataModule, RepoModule, NetModule))
    }

    private val internetConnectionManager: InternetConnectionManager by inject()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        repository = FakeRepository()

        repository.currentListVenues = mutableListOf(dummyVenues[0], dummyVenues[1], dummyVenues[2])

        mapViewModel = MapViewModel(repository, internetConnectionManager)

    }

    @Test
    fun loadVenuesToView() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Trigger loading of venues
        mapViewModel.refresh(true)

        // Observe the items to keep LiveData emitting
        mapViewModel.items.observeForTesting {

            // Loding
            Truth.assertThat(mapViewModel.dataLoading.getOrAwaitValue()).isTrue()

            // Execute pending coroutines actions
            mainCoroutineRule.resumeDispatcher()

            // loading is done
            Truth.assertThat(mapViewModel.dataLoading.getOrAwaitValue()).isFalse()

            // And data correctly loaded
            Truth.assertThat(mapViewModel.items.getOrAwaitValue()).hasSize(3)
        }
    }

    @Test
    fun fetchingVenuesGetError() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Set venues return error
        repository.setReturnError(true)

        // StartFetching
        mapViewModel.refresh(true)

        // Observe the items to keep LiveData emitting
        mapViewModel.items.observeForTesting {

            // Loding
            Truth.assertThat(mapViewModel.dataLoading.getOrAwaitValue()).isTrue()

            // Execute pending coroutines actions
            mainCoroutineRule.resumeDispatcher()

            // loading is done
            Truth.assertThat(mapViewModel.dataLoading.getOrAwaitValue()).isFalse()

            // If isDataLoadingError response was error
            Truth.assertThat(mapViewModel.isDataLoadingError.value).isEqualTo(true)
        }
    }
}
