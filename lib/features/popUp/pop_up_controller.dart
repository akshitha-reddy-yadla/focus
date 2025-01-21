import 'package:flutter/services.dart';
import 'package:focus/data/app_info.dart';
import 'package:focus/features/home/home_controller.dart';
import 'package:get/get.dart';

class PopUpController extends GetxController {
  final homeController = Get.find<HomeController>();

  final installedApps = <AppInfo>[].obs;

  var droppedItems = <AppInfo>[].obs;

  static const platform = MethodChannel("com.example.focus");

  @override
  void onInit() {
    super.onInit();
    installedApps.value = homeController.installedApps;
  }

  void moveItemToDropped(AppInfo item) {
    installedApps.remove(item);
    droppedItems.add(item);
  }

  void removeItemFromDropped(AppInfo item) {
    droppedItems.remove(item);
    installedApps.add(item);
  }

  onContinue() async {
    print("continue");
    List<String> packages = [];

    droppedItems.forEach((element) {
      packages.add(element.packageName.toString());
    });

    print(packages);

    packages = ["com.instagram.android"];

    try {
      final String result =
          await platform.invokeMethod('blockAccess', {"packages": packages});

      if (result == "blocked") {
        print("bloacked");
        Get.back();
      } else {
        print("App allowed");
      }
    } on PlatformException catch (e) {
      print("Failed to get app usage: ${e.message}");
    }
  }
}
