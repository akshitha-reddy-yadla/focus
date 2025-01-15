import 'dart:async';

import 'package:curtail/data/app_data.dart';
import 'package:curtail/data/app_info.dart';
import 'package:curtail/features/main/tabs/app_control/widgets/timer.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';

class ActionController extends GetxController {
  final isLoading = true.obs;

  final installedApps = <AppData>[].obs;
  // final updateList = <AppInfo>[].obs;

  final hours = 0.obs;
  final minutes = 0.obs;
  Timer? _timer; // Timer instance

  @override
  void onInit() {
    super.onInit();
    getInstalledApps();
    // openUsageSettings();
  }

  @override
  void dispose() {
    _timer?.cancel(); // Cancel the timer when widget is disposed
    super.dispose();
  }

  // void openUsageSettings() {
  //   const platform = MethodChannel('com.yourcompany.screenTime');
  //   platform.invokeMethod('openUsageSettings');
  // }

  getInstalledApps() async {
    isLoading(true);
    try {
      const MethodChannel channel = MethodChannel('installed_apps');

      final List<Object?> usage = await channel.invokeMethod("installed_apps");

      List<Map<String, dynamic>> listOfinstalledApps =
          usage.map((e) => Map<String, dynamic>.from(e as Map)).toList();

      List<AppInfo> apps = [];

      for (var i = 0; i < listOfinstalledApps.length; i++) {
        AppInfo info = AppInfo.fromJson(listOfinstalledApps[i]);

        apps.add(info);
      }

      List<AppData> formatedAppData = [];

      for (AppInfo app in apps) {
        AppData appData = AppData(
            id: app.appName! + app.packageName!,
            name: app.appName!,
            icon: app.appLogo,
            packageName: app.packageName!,
            versionName: "",
            versionCode: 0,
            installedTimestamp: 0,
            // versionName: app.versionName,
            // versionCode: app.versionCode,
            // builtWith: app.builtWith,
            // installedTimestamp: app.installedTimestamp,
            isRestricted: false);

        print(app.appLogo);
        print("appData ${appData.toString()} ${appData.icon}");

        formatedAppData.add(appData);

        installedApps(formatedAppData);
      }
    } on PlatformException catch (e) {
      print(e);
    }
    // List<AppInfo> apps = await InstalledApps.getInstalledApps(true, true);

    isLoading(false);

    // print(apps);
  }

  onToggle(String id) {
    installedApps.asMap().forEach((key, value) {
      if (value.id == id) {
        value.isRestricted = !value.isRestricted;
      }
    });

    installedApps.refresh();
  }

  onSelectTimer() {
    Get.bottomSheet(const TimerScreen());
  }

  onHourChange(value) {
    hours(value);
  }

  onMinutesChange(value) {
    minutes(value);
  }

  onTimerSet(
    String id,
  ) {
    installedApps.asMap().forEach((key, value) {
      if (value.id == id) {
        // save time
      }
    });
    Get.back();
  }

  // static const platform = MethodChannel('com.curtail.screenTime');

  // Future<void> restrictApps(List<String> appPackageNames) async {
  //   try {
  //     await platform.invokeMethod('kioskModeLock', {'apps': appPackageNames});
  //   } on PlatformException catch (e) {
  //     print("Failed to restrict apps: '${e.message}'.");
  //   }
  // }

  onDone() {
    List<String> listOfAppsToRestrict = [];
    for (var i = 0; i < installedApps.length; i++) {
      if (installedApps[i].isRestricted) {
        listOfAppsToRestrict.add(installedApps[i].packageName);
      }
    }
    // restrictApps(listOfAppsToRestrict);
  }
}

Widget timerDialog() {
  return Container(
    padding: const EdgeInsets.all(20),
    color: Colors.white,
    child: Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        const Text(
          'This is a Bottom Modal Sheet!',
          style: TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 20),
        const Text(
          'You can add any content you like here, such as text, buttons, etc.',
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 20),
        ElevatedButton(
          onPressed: () {
            Get.back(); // Close the bottom sheet
          },
          child: const Text('Close'),
        ),
      ],
    ),
  );
}
