import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:focus/data/app_info.dart';
import 'package:focus/features/popUp/pop_up_screen.dart';
import 'package:get/get.dart';

class HomeController extends GetxController {
  final screenTime = "".obs;

  final installedApps = <AppInfo>[].obs;

  final platform = const MethodChannel("com.example.focus");

  @override
  void onInit() {
    super.onInit();
    getData();
  }

  getData() {
    getAppUsage();
    getPhoneStats();
  }

  void getAppUsage() async {
    final List<Object?> usage = await platform.invokeMethod("getAppUsage");

    List<Map<String, dynamic>> listOfinstalledApps =
        usage.map((e) => Map<String, dynamic>.from(e as Map)).toList();

    List<AppInfo> apps = [];

    for (var i = 0; i < listOfinstalledApps.length; i++) {
      AppInfo info = AppInfo.fromJson(listOfinstalledApps[i]);

      apps.add(info);
    }

    installedApps.value = apps;
  }

  void getPhoneStats() async {
    final String usage = await platform.invokeMethod("getScreenTimeUsage");

    print("usage ${usage}");

    screenTime.value = usage;
  }

  void openBottomSheet(BuildContext context) {
    showModalBottomSheet(
      isScrollControlled: true,
      context: context,
      builder: (BuildContext context) {
        return Container(
          height: MediaQuery.sizeOf(context).height - 50,
          decoration: const BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.only(
              topLeft: Radius.circular(16.0),
              topRight: Radius.circular(16.0),
            ),
          ),
          child: const Center(child: PopUpScreen()),
        );
      },
    );
  }
}
