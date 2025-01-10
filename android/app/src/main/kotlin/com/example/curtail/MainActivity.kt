package com.example.curtail

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.provider.Settings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.curtail.screenTime"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger, CHANNEL
        ).apply {
            setMethodCallHandler { call, result ->
                run {
                    System.out.print(call.method)
                    if (call.method.equals("getScreenTime")) {
                        result.success(getScreenTime());
                    } else {
                        result.notImplemented();
                    }
                }
            }
        }
    }


    private fun getScreenTime(): String {
        // Check if UsageStatsManager is available (API level 21+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Use applicationContext.getSystemService to ensure context is valid
            val usageStatsManager =
                applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager?

            if (usageStatsManager == null) return "0:0"

            val currentTime = System.currentTimeMillis()
            // Convert milliseconds to Date
            val date = Date(currentTime)
            // Create a SimpleDateFormat with the desired format
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            // Set the time zone to the default local time zone
            format.timeZone = TimeZone.getDefault()

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val beginTime = calendar.timeInMillis // Start of today (midnight)
            System.out.println(beginTime);
            System.out.println("time");
            System.out.println(currentTime);
            val stats: List<UsageStats> = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, beginTime, currentTime
            )

            var totalScreenTime: Long = 0
            for (usageStats in stats) {
                System.out.println(usageStats.totalTimeInForeground)
                totalScreenTime += usageStats.totalTimeInForeground
            }

            val hours =
                (totalScreenTime / 3600000).toInt() // Convert milliseconds to hours (1 hour = 3,600,000 milliseconds)
            val minutes = ((totalScreenTime % 3600000) / 60000).toInt()

            return "$hours : $minutes";
        } else {
            // For devices below API 21, return 0 or handle it accordingly
            return "0:0"
        }
    }

    // Method to open Usage Access Settings if needed (for Android versions 5.0 and above)
    private fun openUsageSettings() {
        // Use startActivity to open the Usage Access Settings
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }
}