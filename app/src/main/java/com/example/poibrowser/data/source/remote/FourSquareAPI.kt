package com.example.poibrowser.data.source.remote

import com.example.poibrowser.BuildConfig
import com.example.poibrowser.data.model.FourSquareResponse
import com.example.poibrowser.data.source.remote.APIConstants.Companion.API_URL_SEARCH
import com.example.poibrowser.data.source.remote.APIConstants.Companion.API_VENUE_DETAIL
import com.example.poibrowser.data.source.remote.APIConstants.Companion.CONTENT_TYPE
import com.example.poibrowser.data.source.remote.APIConstants.Companion.PLACES_LIMIT
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author Tomislav Curis
 */
interface FourSquareAPI {

    @Headers(CONTENT_TYPE)
    @GET(API_URL_SEARCH)
    suspend fun searchVenues(
        @Query("client_id") client_id: String = BuildConfig.FOURSQUARE_CLIENT_ID,
        @Query("client_secret") client_secret: String = BuildConfig.FOURSQUARE_CLIENT_SECRET,
        @Query("v") v: String = BuildConfig.FOURSQUARE_VERSION,
        @Query("ll") ll: String,
        @Query("radius") radius: String,
        @Query("limit") limit: String = PLACES_LIMIT
    ): Response<FourSquareResponse>

    @Headers(CONTENT_TYPE)
    @GET("$API_VENUE_DETAIL/{venue_id}")
    suspend fun getVenueDetail(
        @Path("venue_id") venue_id:  String,
        @Query("client_id") client_id: String = BuildConfig.FOURSQUARE_CLIENT_ID,
        @Query("client_secret") client_secret: String = BuildConfig.FOURSQUARE_CLIENT_SECRET,
        @Query("v") v: String = BuildConfig.FOURSQUARE_VERSION
    ): Response<FourSquareResponse>
}