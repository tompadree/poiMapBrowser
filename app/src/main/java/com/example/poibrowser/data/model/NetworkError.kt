package com.example.poibrowser.data.model


/**
 * @author Tomislav Curis
 */
import com.google.gson.annotations.SerializedName

data class NetworkError(

    @SerializedName("message")
    val message: String,

    @SerializedName("meta")
    val meta: Meta

)

data class Meta(

    @SerializedName("status")
    val status: String,
    @SerializedName("msg")
    val msg: String
)