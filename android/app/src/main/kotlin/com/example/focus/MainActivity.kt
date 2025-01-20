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
import java.util.concurrent.TimeUnit
import kotlin.math.abs


class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.focus"
    private val REQUEST_CODE = 101

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
                result.success(requestPackagePermission())
//                result.success(startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)))
            } else if (call.method == "getScreenTimeUsage") {
                result.success(getStats())
            } else if (call.method == "getAppUsage") {
                result.success(getAppUsage())
            } else {
                result.notImplemented()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStats(): String {

        return getScreenUsage();

    }

    private fun requestPackagePermission() {

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

        if (requestCode == REQUEST_CODE) {
            for (i in permissions.indices) {
                val permission = permissions[i]
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                } else {
                    // Permission denied
                }
            }
        }
    }

    // In your Platform Channel handler
    fun getAppIconBase64(packageName: String): String? {
        return try {
            val appIcon: Drawable = packageManager.getApplicationIcon(packageName)
            val bitmap = drawableToBitmap(appIcon)
            bitmapToBase64(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAppUsage(): MutableList<Map<String, Any>> {

        val usageStatsManager =
            applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val appList: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            System.currentTimeMillis() - 1000 * 3600 * 24,
            System.currentTimeMillis()
        )

        val resultList = mutableListOf<Map<String, Any>>();

        val packageManager = context.packageManager


        if (!appList.isNullOrEmpty()) {

            for (usageStats in appList) {
                val isInstalled = isAppInstalled(context, usageStats.packageName)
                if (isInstalled) {
                    val appInfo = packageManager.getApplicationInfo(usageStats.packageName, 0);

                    if (isSystemApp(appInfo)) {
                        continue
                    }

                    var appName: String? = null
                    try {
                        appName = packageManager.getApplicationLabel(appInfo).toString()
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                        appName = null // Handle the exception if the package name is not found
                    }

                    val app = mapOf(
                        "id" to usageStats.packageName + appName,
                        "usageTime" to usageStats.totalTimeInForeground,
                        "packageName" to usageStats.packageName,
                        "appName" to (appName ?: ""),
                        "appIcon" to (getAppIconBase64(usageStats.packageName) ?: ""),
                    )

                    if (resultList.contains(app)) {
                        System.out.println("data not added to list: ${appName} : ${usageStats.packageName}")
                        continue
                    }

                    System.out.println("data add to list: ${appName} : ${usageStats.packageName}")

                    resultList.add(app);
                } else {
                }
            }
        }

        return resultList

    }

    private fun isSystemApp(appInfo: android.content.pm.ApplicationInfo): Boolean {
        return (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0 || (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
    }

    private fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            // Try to get package info for the specified package name
            context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true  // App is installed
        } catch (e: PackageManager.NameNotFoundException) {
            // The app is not installed, handle the exception gracefully
            false  // App is not installed
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
    private fun getScreenUsage(): String {


        val usageStatsManager =
            applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val appList: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            System.currentTimeMillis() - 1000 * 3600 * 24,
            System.currentTimeMillis()
        )

        var totalTime: Long = 0;

        val packageManager = context.packageManager


        if (!appList.isNullOrEmpty()) {

            for (usageStats in appList) {

                val isInstalled = isAppInstalled(context, usageStats.packageName)
                if (isInstalled) {

                    val appInfo = packageManager.getApplicationInfo(usageStats.packageName, 0);

                    if (isSystemApp(appInfo)) {
                        continue
                    }



                    totalTime += usageStats.totalTimeInForeground;

                } else {
                }
            }
        }


        return convertMillisToHoursMinutes(totalTime);

    }

    private fun convertMillisToHoursMinutes(milliseconds: Long): String {
        val time: String = String.format(
            "%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds),
            TimeUnit.MILLISECONDS.toMinutes(milliseconds) % TimeUnit.HOURS.toMinutes(1)
        )


        val timeParts = time.split(":")

        val hours = timeParts[0].toInt()
        val minutes = timeParts[1].toInt()

        return "${hours}h ${minutes}m";
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

    // Function to convert Drawable to Bitmap
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    // Function to convert Bitmap to Base64 string
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
