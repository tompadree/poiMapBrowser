package com.example.poibrowser.data.source

import com.example.poibrowser.utils.MainCoroutineRule
import com.example.poibrowser.data.model.Result
import com.example.poibrowser.data.source.local.POIBDaoTest.Companion.dummyLatLng
import com.example.poibrowser.data.source.local.POIBDaoTest.Companion.dummyVenues
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals

/**
 * @author Tomislav Curis
 */

@ExperimentalCoroutinesApi
class MapRepositoryTest {
        // Dataset
        private val venueObject1 = dummyVenues[0]
        private val venueObject2 = dummyVenues[1]
        private val venueObject3 = dummyVenues[2]
        private val remoteVenues = listOf(venueObject3, venueObject1).sortedBy { it.id }
        private val localVenues = listOf(venueObject3, venueObject2).sortedBy { it.id }
        private val newVenues = listOf(venueObject1, venueObject2).sortedBy { it.id }

        private lateinit var remoteMapsDataSource: FakeDataSource
        private lateinit var localMapsDataSource: FakeDataSource

        private lateinit var repository: MapRepositoryImpl

        @get:Rule
        val mainCoroutineRule = MainCoroutineRule()

        @Before
        fun createRepository() {
            localMapsDataSource = FakeDataSource(localVenues.toMutableList())
            remoteMapsDataSource = FakeDataSource(remoteVenues.toMutableList())

            repository = MapRepositoryImpl(localMapsDataSource, remoteMapsDataSource)
        }

        @Test
        fun getVenues_emptyRepositoryAndUninitializedCache() = mainCoroutineRule.runBlockingTest {
            val emptySource = FakeDataSource()
            val tempRepository = MapRepositoryImpl(emptySource, emptySource)

            assertThat(tempRepository.searchVenues(true, dummyLatLng,  700.0) is Result.Success)
                .isTrue()
        }

        @Test
        fun getVenues_venueCacheAfterFirstApiCall() = mainCoroutineRule.runBlockingTest {
            // false trigger is default
            val initial = repository.searchVenues(false, dummyLatLng,  700.0)

            remoteMapsDataSource.venues = newVenues.toMutableList()

            val second = repository.searchVenues(false, dummyLatLng,  700.0)

            // Initial and second should match because no backend is called
            assertThat(second).isEqualTo(initial)
        }

        @Test
        fun getVenues_requestsAllVenuesFromLocalDataSource() = mainCoroutineRule.runBlockingTest {
            // When venues are requested from the local repository
            val venues = repository.searchVenues(false, dummyLatLng,  700.0) as Result.Success

            // Then Venues are loaded from the local data source
            assertThat(venues.data).isEqualTo(localVenues)
        }

        @Test
        fun getVenues_requestsAllVenuesFromRemoteDataSource() = mainCoroutineRule.runBlockingTest {
            // When venues are requested from the fourSquare repository
            val venues = repository.searchVenues(true, dummyLatLng,  700.0) as Result.Success

            // Then venues are loaded from the remote data source
            assertThat(venues.data).isEqualTo(remoteVenues)
        }

        @Test
        fun saveVenues_savesToLocal() = mainCoroutineRule.runBlockingTest {
            // When venues are requested from the fourSquare repository
            val venues = repository.searchVenues(true, dummyLatLng,  700.0) as Result.Success

            // Save venues
            repository.saveVenues(venues.data)

            // Fetch them
            val venuesLocal = repository.searchVenues(true, dummyLatLng,  700.0) as Result.Success

            assertThat(venues.data).isEqualTo(venuesLocal.data)
        }

        @Test
        fun getVenues_WithDirtyCache_venuesAreRetrievedFromRemote() = mainCoroutineRule.runBlockingTest {
            // First call returns from REMOTE
            val venues = repository.searchVenues(false, dummyLatLng,  700.0) as Result.Success

            // Set a different list of venues in REMOTE
            remoteMapsDataSource.venues = newVenues.toMutableList()

            // But if venues are cached, subsequent calls load from cache
            val cachedVenues = repository.searchVenues(false, dummyLatLng,  700.0) as Result.Success
            assertThat(cachedVenues).isEqualTo(venues)

            // Now force remote loading
            val refreshedVenues = repository.searchVenues(true, dummyLatLng,  700.0) as Result.Success

            // venues must be the recently updated in REMOTE
            assertThat(refreshedVenues.data).isEqualTo(newVenues)
        }

        @Test
        fun getVenues_remoteUnavailable_error() = mainCoroutineRule.runBlockingTest {
            // Make remote data source unavailable
            remoteMapsDataSource.venues = null

            // Load venues forcing remote load
            val venues = repository.searchVenues(true, dummyLatLng,  700.0)

            // Result should be an error
            assertThat(venues).isInstanceOf(Result.Error::class.java)
        }

        @Test
        fun getVenues_WithRemoteDataSourceUnavailable_venuesAreRetrievedFromLocal() =
            mainCoroutineRule.runBlockingTest {
                // When the remote data source is unavailable
                remoteMapsDataSource.venues = null

                // The repository fetches from the local source
                assertThat((repository.searchVenues(false, dummyLatLng,  700.0) as Result.Success).data).isEqualTo(localVenues)
            }

        @Test
        fun getVenues_WithBothDataSourcesUnavailable_returnsError() = mainCoroutineRule.runBlockingTest {
            // When both sources are unavailable
            remoteMapsDataSource.venues = null
            localMapsDataSource.venues = null

            // The repository returns an error
            assertThat(repository.searchVenues(true, dummyLatLng,  700.0)).isInstanceOf(Result.Error::class.java)
        }

        @Test
        fun getVenues_refreshVenuesFromRemoteDataSource() = mainCoroutineRule.runBlockingTest {
            // Initial state in db
            val initial = localMapsDataSource.venues

            // Fetch from remote
            val remoteVenuesTemp = repository.searchVenues(true, dummyLatLng,  700.0) as Result.Success

            assertEquals(remoteVenuesTemp.data, remoteVenues)
            assertEquals(remoteVenuesTemp.data, localMapsDataSource.venues)
            assertThat(localMapsDataSource.venues).isEqualTo(initial)
        }

        @Test
        fun getVenues_deleteVenues() = mainCoroutineRule.runBlockingTest {
            // Get venues
            val initialVenues = repository.searchVenues(false, dummyLatLng,  700.0) as? Result.Success

            localMapsDataSource.deleteVenues()

            // Fetch after delete
            val afterDeleteVenues = repository.searchVenues(false, dummyLatLng,  700.0) as? Result.Success

            //check
            assertThat(initialVenues?.data).isNotEmpty()
            assertThat(afterDeleteVenues?.data).isEmpty()
        }
}
