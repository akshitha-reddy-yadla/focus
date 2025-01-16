package com.example.focus

import android.Manifest
import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Process.myUid
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.example.focus"
    private val REQUEST_CODE = 101  // Define your request code

    @RequiresApi(Build.VERSION_CODES.O)
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->
            if (call.method == "checkPermission") {
                val hasPermission = isUsageStatsPermissionGranted();
                if(hasPermission) {
                    result.success(hasPermission)
                }else {
                    startActivity( Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            } else if(call.method == "grantPermission") {
                result.success(startActivity( Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)))
            }
            else if (call.method == "getScreenTimeUsage") {
                result.success(getStats())
            } else {
                result.notImplemented()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStats(): Map<String, String> {


        val currentTime = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()

        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy HH:mm:ss")
        val localDate = LocalDate.now()   // your current date time
        val startOfDay: LocalDateTime = localDate.atStartOfDay() // date time at start of the date
        val startTime = startOfDay.atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli() // start time to timestamp
        Log.d("Date:", "start date $startTime")
        Log.d("Date:", "current date $currentTime")
        Log.d("Date:", "start date parsed ${currentTime.toDuration(DurationUnit.HOURS)} ${currentTime.toDuration(DurationUnit.MINUTES)} ${currentTime.toDuration(
            kotlin.time.DurationUnit.DAYS)}}")
        Log.d("Date:", "start date parsed ${startOfDay.format(dateFormatter)}")


        val timeOneHourAgo = currentTime - (60 * 60 * 1000);

        Log.d("time", "${startTime}");
        Log.d("time", "${LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()}");
        Log.d("time", "${LocalDateTime.now().minusHours(1).toInstant(ZoneOffset.UTC).toEpochMilli()}");

        val oneHourAgo = System.currentTimeMillis() - 60 * 60 * 1000;

        val total = getScreenUsage(startTime, currentTime, 0);
        val lastHour = getScreenUsage( oneHourAgo, System.currentTimeMillis(), 0)
//        val lastHour = getScreenUsage(LocalDateTime.now().minusHours(1).toInstant(ZoneOffset.UTC).toEpochMilli(), currentTime);
        return mapOf(
            "lastHour" to lastHour,
            "total" to total
        )
    }

    private fun requestPackagePermission() {
        System.out.println("check for permissions");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.PACKAGE_USAGE_STATS) != PackageManager.PERMISSION_GRANTED) {
            // If not granted, request the LOCATION permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.PACKAGE_USAGE_STATS),
                REQUEST_CODE
            )
        } else {
            // If already granted, log it
            Log.d("Permissions", "LOCATION permission already granted")
        }
        // Define permissions to request
//        val permissionsToRequest = arrayOf(
//            Manifest.permission.PACKAGE_USAGE_STATS,
//        )
//
//        // Check if any of the permissions are already granted
//        val permissionsNeeded = permissionsToRequest.filter {
//            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
//        }
//
//        if (permissionsNeeded.isNotEmpty()) {
//            ActivityCompat.requestPermissions(
//                this,
//                permissionsNeeded.toTypedArray(),
//                REQUEST_CODE
//            )
//        } else {
//            // Permissions already granted, proceed with logic
//            Log.d("Permissions", "All requested permissions are already granted")
//        }
//        if (ContextCompat.checkSelfPermission(this.activity, Manifest.permission.PACKAGE_USAGE_STATS)
//            != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this.activity,
//                arrayOf(Manifest.permission.PACKAGE_USAGE_STATS),
//                REQUEST_CODE
//                );
//            // Permission is not granted
//            Log.d("permission", "permission not granted")
//        }else {
//            Log.d("permission", "permission  granted")
//        }

    }

    // Handle the result of permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        System.out.println("on request permission s request");

        if (requestCode == REQUEST_CODE) {
            for (i in permissions.indices) {
                val permission = permissions[i]
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    Log.d("Permission", "$permission granted")
                } else {
                    // Permission denied
                    Log.d("Permission", "$permission denied")
                }
            }
        }
    }

    private fun isUsageStatsPermissionGranted() : Boolean {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // For Android 6.0 (API 23) and above, use checkOpNoThrow()
                appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), packageName)
            } else {
                // For older versions, use the deprecated checkOp()
                appOps.checkOp(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), packageName)
            }
            return mode == AppOpsManager.MODE_ALLOWED
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getScreenUsage(startTime: Long, currentTime: Long, totalScreenTime: Long): String {
        System.out.println("NOt granted 0 ");

        var screenTime = totalScreenTime;

        val usageStatsManager = applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val stats: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTime, currentTime
        )
        Log.d("stats", stats.toString())

        Log.d("screen time", screenTime.toString());

        var hours = 0;
        var minutes = 0;

        for (usageStats in stats) {
            Log.d("stats1", usageStats.toString())
            Log.d("App Usage", "${usageStats.packageName}: ${usageStats.totalTimeInForeground}")
//            screenTime += usageStats.totalTimeInForeground
        }

        for(usageStats in stats) {
            Log.d("stats1 ", usageStats.toString())
            screenTime += usageStats.totalTimeInForeground
        }

        hours = (screenTime / 3600000).toInt()
        minutes = ((screenTime % 3600000) / 60000).toInt()

        System.out.println("${formatDigits(hours)}:${formatDigits(minutes)}");

        return "${formatDigits((screenTime / 3600000).toInt())}:${formatDigits(((screenTime % 3600000) / 60000).toInt())}";

    }

    fun convertMillisToHoursMinutes(milliseconds: Long): String {
        val hours = (milliseconds / 3600000).toInt()  // Convert to hours
        val minutes = ((milliseconds % 3600000) / 60000).toInt()  // Get remaining minutes
        return "${formatDigits(hours)}:${formatDigits(minutes)}"
    }

    private fun formatDigits(num: Int): String {
        return if (abs(num).toString().trim().length == 2) {
            num.toString()
        } else {
            "0$num"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
