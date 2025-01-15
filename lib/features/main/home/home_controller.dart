import 'dart:async';

import 'package:curtail/data/app_info.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

class HomeController extends GetxController {
  Timer? timer;

  var date = "".obs;
  var screenTime = "".obs;
  var screenTimeinLastOneHour = "".obs;
  var pickups = 0.obs;

  final listOfApps = <AppInfo>[].obs;

  @override
  void onInit() {
    super.onInit();
    updateScreenTime();
    getAppUsage();
    timer = Timer.periodic(
        const Duration(seconds: 60), (Timer t) => updateScreenTime());
  }

  @override
  void dispose() {
    timer?.cancel();
    super.dispose();
  }

  updateScreenTime() {
    getTodaysDate();
    getScreenTime();
  }

  void getTodaysDate() {
    DateTime dateTime = DateTime.now();

    String formatedDate =
        "${DateFormat("EEEE").format(dateTime).substring(0, 3)}, ${DateFormat("MMMM").format(dateTime).substring(0, 3)}${DateFormat("d").format(dateTime)}";

    date(formatedDate);
  }

  static const platform = MethodChannel('com.curtail.curtail');

  getScreenTime() async {
    try {
      final Map<Object?, Object?> time =
          await platform.invokeMethod("getScreenTimeUsage");

      var entryList = time.entries.toList();

      screenTime(entryList[1].value.toString());
      screenTimeinLastOneHour(entryList[0].value.toString());
      pickups(int.parse(entryList[2].value.toString()));
    } on PlatformException catch (e) {
      print(e);
    }
  }

  void getAppUsage() async {
    try {
      final List<Object?> usage = await platform.invokeMethod("getScreenUsage");

      List<Map<String, dynamic>> appUsageList =
          usage.map((e) => Map<String, dynamic>.from(e as Map)).toList();

      List<AppInfo> apps = [];

      for (var i = 0; i < appUsageList.length; i++) {
        AppInfo info = AppInfo.fromJson(appUsageList[i]);

        apps.add(info);
      }
      listOfApps.value = apps;
    } on PlatformException catch (e) {
      print(e);
    }
  }
}
