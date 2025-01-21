package com.example.focus.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Log


public class Utils(context: Context) {

    var usageStatsManager: UsageStatsManager? = null
    private var context: Context? = null

    fun Utils(context: Context?) {
        this.context = context
    }
    fun getLauncherTopApp(): String {
        val manager = context!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsManager =
                context?.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val taskInfoList = manager.getAppTasks()

                val taskIn = manager.getRunningTasks(1);

            System.out.print("tsdk ${taskIn}");

            if (null != taskInfoList && !taskInfoList.isEmpty()) {
                return taskInfoList[0].taskInfo.topActivity!!.packageName
            }
        } else {
            val endTime = System.currentTimeMillis()
            val beginTime = endTime - 10000
            var result = ""
            val event = UsageEvents.Event()
            val usageEvents: UsageEvents = usageStatsManager!!.queryEvents(beginTime, endTime)

            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.packageName
                }
            }
            if (!TextUtils.isEmpty(result)) Log.d("RESULT", result)
            return result
        }
        return ""
    }
}