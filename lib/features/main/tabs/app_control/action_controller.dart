import 'dart:async';

import 'package:curtail/data/app_data.dart';
import 'package:curtail/features/main/tabs/app_control/widgets/timer.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:installed_apps/app_info.dart';
import 'package:installed_apps/installed_apps.dart';

class ActionController extends GetxController {
  final isLoading = true.obs;

  final installedApps = <AppData>[].obs;

  // timer

  final hours = 0.obs;
  final minutes = 0.obs;
  Timer? _timer; // Timer instance

  @override
  void onInit() {
    super.onInit();
    getInstalledApps();
    openUsageSettings();
  }

  @override
  void dispose() {
    _timer?.cancel(); // Cancel the timer when widget is disposed
    super.dispose();
  }

  void openUsageSettings() {
    const platform = MethodChannel('com.yourcompany.screenTime');
    platform.invokeMethod('openUsageSettings');
  }

  getInstalledApps() async {
    isLoading(true);
    List<AppInfo> apps = await InstalledApps.getInstalledApps(true, true);
    List<AppData> formatedAppData = [];

    for (AppInfo app in apps) {
      AppData appData = AppData(
          id: app.name + app.packageName,
          name: app.name,
          icon: app.icon,
          packageName: app.packageName,
          versionName: app.versionName,
          versionCode: app.versionCode,
          builtWith: app.builtWith,
          installedTimestamp: app.installedTimestamp,
          isRestricted: false);

      print(app.icon);
      print("appData ${appData.toString()} ${appData.icon}");

      formatedAppData.add(appData);
    }

    installedApps(formatedAppData);
    isLoading(false);

    print(apps);
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

  var platform = const MethodChannel('com.curtail.kioskMode');

  Future<void> enableKioskMode() async {
    try {
      await platform.invokeMethod('enableKioskMode');
    } on PlatformException catch (e) {
      print("Failed to enable Kiosk mode: ${e.message}");
    }
  }

  onDone() {
    enableKioskMode();
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
