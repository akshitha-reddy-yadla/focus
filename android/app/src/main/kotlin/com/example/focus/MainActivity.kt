package com.example.focus

import android.Manifest
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.io.ByteArrayOutputStream
import java.sql.Date
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.abs


class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.focus"
    private val REQUEST_CODE = 101  // Define your request code

    @RequiresApi(Build.VERSION_CODES.O)
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger, CHANNEL
        ).setMethodCallHandler { call, result ->
            if (call.method == "checkPermission") {
                val hasPermission = isUsageStatsPermissionGranted();
                if (hasPermission) {
                    result.success(hasPermission)
                } else {
                    startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            } else if (call.method == "grantPermission") {
                result.success(startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)))
            } else if (call.method == "getScreenTimeUsage") {
                result.success(getStats())
            } else {
                result.notImplemented()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStats(): Map<String, Any> {

        return getScreenUsage();


//        val currentTime =
//            LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
//
//        val startTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
//            .toEpochMilli() // start time to timestamp
//        Log.d("Date:", "start date $startTime")
//        Log.d("Date:", "current date $currentTime")
//        Log.d(
//            "Date:",
//            "start date parsed ${currentTime.toDuration(DurationUnit.HOURS)} ${
//                currentTime.toDuration(DurationUnit.MINUTES)
//            } ${
//                currentTime.toDuration(
//                    kotlin.time.DurationUnit.DAYS
//                )
//            }}"
//        )
////        Log.d("Date:", "start date parsed ${startOfDay.format(dateFormatter)}")
//
//        Log.d("Zone:", "start date parsed ${ZoneId.systemDefault()}")


//        val timeOneHourAgo = currentTime - (60 * 60 * 1000);
//
//        Log.d("time", "${startTime}");
//        Log.d("time", "${currentTime}");
//        Log.d(
//            "time",
//            "${
//                LocalDateTime.now().minusHours(1).atZone(ZoneId.systemDefault()).toInstant()
//                    .toEpochMilli()
//            }"
//        );
//        Log.d(
//            "time",
//            "${LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()}"
//        );
//
//        val oneHourAgo = System.currentTimeMillis() or -TimeUnit.HOURS.toMillis(1)
//
////        val total = getScreenUsage(startTime, currentTime, 0);
//        val lastHour = getScreenUsage(
//            oneHourAgo,
//            System.currentTimeMillis(), 0
//        )
//        val lastHour = getScreenUsage(LocalDateTime.now().minusHours(1).toInstant(ZoneOffset.UTC).toEpochMilli(), currentTime);
//        return mapOf(
//            "lastHour" to lastHour,
////            "total" to total
//        )
    }

    private fun requestPackagePermission() {
        System.out.println("check for permissions");

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.PACKAGE_USAGE_STATS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If not granted, request the LOCATION permission
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.PACKAGE_USAGE_STATS), REQUEST_CODE
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
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
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

    private fun isUsageStatsPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // For Android 6.0 (API 23) and above, use checkOpNoThrow()
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName
                )
            } else {
                // For older versions, use the deprecated checkOp()
                appOps.checkOp(
                    AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName
                )
            }
            return mode == AppOpsManager.MODE_ALLOWED
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getScreenUsage(): Map<String, Any> {
        System.out.println("NOt granted 0 ");

//        var screenTime = totalScreenTime;

        val usageStatsManager =
            applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

//        val stats: List<UsageStats> = usageStatsManager.queryUsageStats(
//            UsageStatsManager.INTERVAL_DAILY, startTime, currentTime
//        )

//        for (usageStats in stats) {
////            Log.d("stats1 ", usageStats.toString())
//            Log.d("screenTime ", "${usageStats.packageName} sdf ${screenTime.toString()}")
//
//            screenTime += usageStats.totalTimeInForeground
//        }

//        System.out.println("${formatDigits((screenTime / 3600000).toInt())}:${formatDigits(((screenTime % 3600000) / 60000).toInt())}");

        println("time print");
//        System.out.println(convertMillisToHoursMinutes(screenTime));

        val appList: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            System.currentTimeMillis() - 1000 * 3600 * 24,
            System.currentTimeMillis()
        )

        val currentTime = System.currentTimeMillis()


        // Subtract one hour (3600 seconds * 1000 milliseconds)
        val oneHourAgo = currentTime - 1000 * 3600


        // Format the times as UTC date
        val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"))

        System.out.println("Current time (UTC): " + sdf.format(Date(currentTime)))
        System.out.println("One hour ago (UTC): " + sdf.format(Date(oneHourAgo)))


        // Verify if the difference is exactly one hour (3600000 milliseconds)
        println("Difference (milliseconds): " + (currentTime - oneHourAgo))


        val appListForLastOneHour: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            System.currentTimeMillis() - 1000 * 3600,
            System.currentTimeMillis()
        )


//        val mySortedMap: SortedMap<Long, UsageStats> = sortedMapOf()

        val statsMap: MutableMap<String, Any> = HashMap()

        var totalTime: Long = 0;
        var timeUsedInLastOneHour: Long = 0;

        val packageManager = context.packageManager


        if (!appList.isNullOrEmpty()) {

            for (usageStats in appList) {

                var appIcon: Drawable?
                try {
                    appIcon = packageManager.getApplicationIcon(usageStats.packageName)
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                    appIcon = null // Handle the exception
                }

                var iconBitmap: Bitmap? = null
                if (appIcon != null) {
                    iconBitmap = drawableToBitmap(appIcon)
                }


// Convert Bitmap to Base64 string
                var base64Icon: String? = null
                if (iconBitmap != null) {
                    base64Icon = bitmapToBase64(iconBitmap)
                }

                var appName: String? = null
                try {
                    val appInfo = packageManager.getApplicationInfo(packageName, 0)
                    appName = packageManager.getApplicationLabel(appInfo).toString()
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                    appName = null // Handle the exception if the package name is not found
                }


                statsMap["totalTimeInForeground"] = usageStats.totalTimeInForeground
                statsMap["packageName"] = usageStats.packageName
                statsMap["appIcon"] = base64Icon ?: ""
                statsMap["appName"] = appName ?: ""

                totalTime += usageStats.totalTimeInForeground;
            }
            println("sd");
            System.out.println(totalTime);
//            System.out.println(mySortedMap.toString());
        }

        if (!appListForLastOneHour.isNullOrEmpty()) {
            for (usageStats in appListForLastOneHour) {
                timeUsedInLastOneHour += usageStats.totalTimeInForeground;
            }
        }
        println("sd1");
        System.out.println(timeUsedInLastOneHour);
        System.out.println(System.currentTimeMillis() - 1000 * 3600);
        System.out.println(System.currentTimeMillis());

        val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = ZonedDateTime.now().toInstant().toEpochMilli()

        val oneHour = ZonedDateTime.now().minusHours(1).toInstant().toEpochMilli();

        val stats = usageStatsManager.queryAndAggregateUsageStats(start, end)

        val total = Duration.ofMillis(stats.values.map { it.totalTimeInForeground }.sum())
        println("YOU SPENT ${total.toHours()} mins.");

        val hourStats = usageStatsManager.queryAndAggregateUsageStats(oneHour, end);
        println("YOU SPENT ${Duration.ofMillis(hourStats.values.map { it.totalTimeInForeground }.sum()).toHours()} mins.");


        return mapOf(
            "lastHour" to convertMillisToHoursMinutes(timeUsedInLastOneHour),
            "total" to convertMillisToHoursMinutes(totalTime),
            "stats" to statsMap
        )

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

//    ltMap.put("iconBase64", base64Icon);
//    result.success(resultMap);

    // Helper method to convert Drawable to Bitmap
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        var bitmap: Bitmap = Bitmap.createBitmap(
            drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888
        );
        var canvas: Canvas = Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    // Helper method to convert Bitmap to Base64
    private fun bitmapToBase64(bitmap: Bitmap): String {
        var byteArrayOutputStream: ByteArrayOutputStream = ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
