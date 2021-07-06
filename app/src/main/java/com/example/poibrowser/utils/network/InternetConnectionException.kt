package com.example.poibrowser.utils.network

import java.io.IOException

/**
 * @author Tomislav Curis
 */
class InternetConnectionException : IOException()

class NoConnectivityException : IOException() {
    override val message: String
        get() =
            "No network available, please check your WiFi or Data connection"
}

class NoInternetException() : IOException() {
    override val message: String
        get() =
            "No internet available, please check your connected WIFi or Data"
}