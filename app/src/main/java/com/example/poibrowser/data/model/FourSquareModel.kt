package com.example.poibrowser.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.lang.Exception

/**
 * @author Tomislav Curis
 */

data class FourSquareResponse(
    @SerializedName("meta")
    val meta: FourSquareResponseMetaObject,

    @SerializedName("response")
    val response: FourSquareResponseObject
)

data class FourSquareResponseObject(
    @SerializedName("confident")
    val confident: Boolean,

    @SerializedName("venues")
    val venues: List<FourSquareModel>,

    @SerializedName("venue")
    val venue: FourSquareModel
)

data class FourSquareResponseMetaObject(
    @SerializedName("code")
    val code: Int,

    @SerializedName("errorType")
    val errorType: String,

    @SerializedName("errorDetail")
    val errorDetail: String
)

@Entity(tableName = "venues")
data class FourSquareModel(
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("contact")
    val contact: FourSquareModelContact,

    @SerializedName("location")
    @Embedded(prefix = "location_")
    val location: FourSquareModelLocation,

    @SerializedName("canonicalUrl")
    val canonicalUrl: String?

//    @SerializedName("categories")
//    val categories: List<FourSquareModelCategoriesObject>

)

data class FourSquareModelContact(
    @SerializedName("phone")
    val phone: String,

    @SerializedName("twitter")
    val twitter: String
)

data class FourSquareModelLocation(

    @SerializedName("lat")
    val lat: String,

    @SerializedName("lng")
    val lng: String,

    @SerializedName("formattedAddress")
    val formattedAddress: List<String>

)

data class FourSquareModelCategoriesObject(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("pluralName")
    val pluralName: String

) : Serializable

class CategoriesConverter {

    companion object {
        @TypeConverter
        @JvmStatic
        fun stringToCategories(value: String): ArrayList<FourSquareModelCategoriesObject> {
            val listType = object : TypeToken<List<String>>() {}.type

            try {
            return Gson().fromJson(value, listType)
            } catch (e: Exception) {
                e.printStackTrace()
                return ArrayList()
            }
        }

        @TypeConverter
        @JvmStatic
        fun fromCategoriesToString(list: List<FourSquareModelCategoriesObject>): String = Gson().toJson(list)
    }
}

class FormattedAddressConverter {

    companion object {
        @TypeConverter
        @JvmStatic
        fun stringToAddress(value: String): ArrayList<String> {
            val listType = object : TypeToken<List<String>>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        @JvmStatic
        fun fromAddressToString(list: List<String>): String = Gson().toJson(list)
    }
}

class FourSquareModelContactConverter {

    @TypeConverter
    fun stringToObject(obj: String): FourSquareModelContact? =
        Gson().fromJson(obj, FourSquareModelContact::class.java)

    @TypeConverter
    fun fromObjectToString(obj: FourSquareModelContact): String?
            = Gson().toJson(obj)
}

class FourSquareModelLocationConverter {

    @TypeConverter
    fun stringToObject(obj: String): FourSquareModelLocation? =
        Gson().fromJson(obj, FourSquareModelLocation::class.java)

    @TypeConverter
    fun fromObjectToString(obj: FourSquareModelLocation): String?
            = Gson().toJson(obj)
}