package com.example.poibrowser.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.poibrowser.data.model.*

/**
 * @author Tomislav Curis
 */

@Database(entities = [FourSquareModel::class], version = 1, exportSchema = false)
@TypeConverters(CategoriesConverter::class, FourSquareModelContactConverter::class,
    FourSquareModelLocationConverter::class, FormattedAddressConverter::class)
abstract class POIBDatabase: RoomDatabase() {
    abstract fun getPOIBDao(): POIBDao
}