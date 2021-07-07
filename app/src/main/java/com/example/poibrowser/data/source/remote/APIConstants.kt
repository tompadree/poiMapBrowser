package com.example.poibrowser.data.source.remote

/**
 * @author Tomislav Curis
 */
interface APIConstants {

    companion object {

        const val BASE_URL = "https://api.foursquare.com"

        const val API_URL_SEARCH = "/v2/venues/search"

        const val API_VENUE_DETAIL = "/v2/venues"

        const val CONTENT_TYPE = "Content-Type: application/json; charset=utf-8"

        const val PLACES_LIMIT = "500"
    }
}