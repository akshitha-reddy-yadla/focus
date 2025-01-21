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
    private val REQUEST_CODE = 1001
    private val BLOCKED_APPS_KEY = "blocked_apps"
    private var blockedPackages: List<String> = ArrayList()

    var appReceiver: BlockedAppReceiver = BlockedAppReceiver()


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
            } else if (call.method == "blockAccess") {
                blockedPackages = call.argument("packages")!!

                val intent = Intent(this, BlockedAppReceiver::class.java)
                sendBroadcast(intent)
            } else if (call.method.equals("unblockApps")) {
//                blockedPackages.clear();
            } else {
                result.notImplemented()
            }
        }
    }

    // Save the list of blocked apps to SharedPreferences
    private fun saveBlockedApps(blockedPackages: List<String>) {
        val sharedPreferences = getSharedPreferences("AppBlockerPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Convert List to Set for SharedPreferences compatibility
        val blockedAppsSet = HashSet(blockedPackages)  // Using HashSet to convert List to Set
    }


    // Clear the list of blocked apps from SharedPreferences
    private fun clearBlockedApps() {
        val sharedPreferences = getSharedPreferences("AppBlockerPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(BLOCKED_APPS_KEY)
        editor.apply()
    }

    // Start monitoring app launches for blocked apps
    private fun startMonitoringBlockedApps() {
        // Register BroadcastReceiver to monitor all app launches
        val intent = Intent(this, BlockedAppReceiver::class.java)
        intent.setAction("com.example.MONITOR_APPS")
        sendBroadcast(intent)
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
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.PACKAGE_USAGE_STATS), REQUEST_CODE
            )
        } else {
            // If already granted, log it
            Log.d("Permissions", "LOCATION permission already granted")
        }
    }

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
            context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }


    private fun isUsageStatsPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName
                )
            } else {
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

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun checkAndBlockAppUsage(
        blockedPackages: List<String>?,
        result: MethodChannel.Result
    ) {
        val usm = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()


        // Get usage stats for the last 10 seconds
        val stats = usm.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            currentTime - 1000 * 10000,
            currentTime
        )

        if (stats != null && !stats.isEmpty()) {
            // Find the most recently used app
            var lastUsageStats: UsageStats? = null
            var lastTimeUsed: Long = 0

            for (usageStats in stats) {
                if (usageStats.lastTimeUsed > lastTimeUsed) {
                    lastUsageStats = usageStats
                    lastTimeUsed = usageStats.lastTimeUsed
                }
            }

            if (lastUsageStats != null) {
                val currentPackage = lastUsageStats.packageName
                Log.d("AppBlocker", "Current package: $currentPackage")

                System.out.println(blockedPackages.toString());

                if (blockedPackages != null && blockedPackages.contains(currentPackage)) {
                    // If the app being used is blocked, return "Blocked"
                    result.success("Blocked")
                } else {
                    result.success("Allowed")
                }
            } else {
                result.success("No Usage")
            }
        } else {
            result.success("No Usage")
        }
    }
}
