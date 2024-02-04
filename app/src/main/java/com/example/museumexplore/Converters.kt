package com.example.museumexplore

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    @TypeConverter
    fun fromString(value: String?): Map<String, Double>? {
        if (value == null) {
            return null
        }
        val mapType = object : TypeToken<Map<String, Double>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun toString(value: Map<String, Double>?): String? {
        if (value == null) {
            return null
        }
        return Gson().toJson(value)
    }

//    private val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

//    @TypeConverter
//    fun fromDateToString(date: Date?): String? {
//        return date?.let { dateFormat.format(it) }
//    }
//
//    @TypeConverter
//    fun fromStringToDate(dateString: String?): Date? {
//        return dateString?.let {
//            try {
//                dateFormat.parse(it)
//            } catch (e: ParseException) {
//                null
//            }
//        }
//    }
}