package com.example.focus.utils

import android.content.Context
import android.content.SharedPreferences


class SharedPrefUtil {

    private val SHARED_APP_PREFERENCE_NAME = "SharedPref"
    var cxt: Context? = null
    private val EXTRA_LAST_APP = "EXTRA_LAST_APP"
    private var pref: SharedPreferences? = null
    private val mEditor: SharedPreferences.Editor? = null

    fun SharedPrefUtil(context: Context) {
        this.pref = context.getSharedPreferences(SHARED_APP_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun getInstance(context: Context) {
        return SharedPrefUtil(context)
    }


    fun putString(key: String?, value: String?) {
        pref!!.edit().putString(key, value).apply()
    }

    fun putInteger(key: String?, value: Int) {
        pref!!.edit().putInt(key, value).apply()
    }

    fun putBoolean(key: String?, value: Boolean) {
        pref!!.edit().putBoolean(key, value).apply()
    }

    fun getString(key: String?): String? {
        return pref!!.getString(key, "")
    }

    fun getInteger(key: String?): Int {
        return pref!!.getInt(key, 0)
    }

    fun getBoolean(key: String?): Boolean {
        return pref!!.getBoolean(key, false)
    }

    fun getLastApp(): String? {
        return getString(EXTRA_LAST_APP)
    }

    fun setLastApp(packageName: String?) {
        putString(EXTRA_LAST_APP, packageName)
    }

    fun clearLastApp() {
        pref!!.edit().remove(EXTRA_LAST_APP)
    }

    //add apps to locked list
    fun createLockedAppsList(appList: List<String?>) {
        for (i in appList.indices) {
            putString("app_$i", appList[i])
        }
        putInteger("listSize", appList.size)
    }

    //get apps from locked list
    fun getLockedAppsList(): List<String?> {
        val temp: MutableList<String?> = ArrayList()
        val size = getInteger("listSize")
        for (i in 0 until size) {
            temp.add(getString("app_$i"))
        }
        return temp
    }

    fun setLockedAppsListProfile(appList: List<String?>) {
        for (i in appList.indices) {
            putString("profileApp_$i", appList[i])
        }
        putInteger("profileListSize", appList.size)
    }

    fun getLockedAppsListProfile(): List<String?> {
        val temp: MutableList<String?> = ArrayList()
        val size = getInteger("profileListSize")
        for (i in 0 until size) {
            temp.add(getString("profileApp_$i"))
        }
        return temp
    }

    fun setDaysList(daysList: List<String?>) {
        for (i in daysList.indices) {
            putString("day_$i", daysList[i])
        }
        putInteger("daysListSize", daysList.size)
    }

    fun getDaysList(): List<String?> {
        val temp: MutableList<String?> = ArrayList()
        val size = getInteger("daysListSize")
        for (i in 0 until size) {
            temp.add(getString("day_$i"))
        }
        return temp
    }

    //start time
    fun setStartTimeHour(date: String?) {
        putString("start_hour", date)
    }

    fun getStartTimeHour(): String? {
        return getString("start_hour")
    }

    fun setStartTimeMinute(date: String?) {
        putString("start_minute", date)
    }

    fun getStartTimeMinute(): String? {
        return getString("start_minute")
    }

    //endTime
    fun setEndTimeHour(date: String?) {
        putString("end_hour", date)
    }

    fun getEndTimeHour(): String? {
        return getString("end_hour")
    }

    fun setEndTimeMinute(date: String?) {
        putString("end_minute", date)
    }

    fun getEndTimeMinute(): String? {
        return getString("end_minute")
    }

}