package com.example.poibrowser.data.model


/**
 * @author Tomislav Curis
 */
import com.google.gson.annotations.SerializedName

data class NetworkError(

    @SerializedName("meta")
    val meta: FourSquareResponseMetaObject

)
