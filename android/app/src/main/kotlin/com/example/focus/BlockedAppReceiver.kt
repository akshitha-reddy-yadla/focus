package com.example.focus

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.focus.activity.ScreenBlocker
import com.example.focus.utils.Utils


class BlockedAppReceiver : BroadcastReceiver() {

    private val BLOCKED_APPS_KEY = "blocked_apps"


     fun killThisPackageIfRunning(context: Context, packageName: String) {
        var activityManager: ActivityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager;

        activityManager.killBackgroundProcesses(packageName);

    }

    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.getStringExtra(Intent.EXTRA_PACKAGE_NAME)

        val utils: Utils = Utils(context);

        val appRunning = utils.getLauncherTopApp();


        // Get the list of blocked apps from SharedPreferences
        val sharedPreferences =
            context.getSharedPreferences("AppBlockerPrefs", Context.MODE_PRIVATE)
        val blockedApps = sharedPreferences.getStringSet(BLOCKED_APPS_KEY, null)

        println(blockedApps);

        if(blockedApps!!.contains(appRunning)) {
            killThisPackageIfRunning(context, appRunning);
            val i = Intent(context, ScreenBlocker::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            i.putExtra("broadcast_receiver", "broadcast_receiver")
            context.startActivity(i)
        }

        System.out.println("receiver ${blockedApps}")
        System.out.println("receiver ewe ${packageName}")

        // If the launched app is in the blocked list, prevent it from opening
//        if (blockedApps != null && blockedApps.contains(packageName)) {
//
//            killThisPackageIfRunning(context, appRunning);
//            try {
//                // Send the user to the home screen or show a block screen
//                val homeIntent = Intent(Intent.ACTION_MAIN)
//                homeIntent.addCategory(Intent.CATEGORY_HOME)
//                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                context.startActivity(homeIntent)
//
//                // Optionally, show a toast to notify user
//                Toast.makeText(context, "This app is blocked", Toast.LENGTH_SHORT).show()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
    }
}