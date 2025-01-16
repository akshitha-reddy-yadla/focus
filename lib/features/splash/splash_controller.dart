import 'package:flutter/services.dart';
import 'package:focus/features/home/home_controller.dart';
import 'package:focus/features/home/home_screen.dart';
import 'package:get/get.dart';

class SplashController extends GetxController {
  @override
  void onInit() {
    super.onInit();
    _checkPermissions();
  }

  void _checkPermissions() async {
    const platform = MethodChannel("com.example.focus");
    final bool permissions = await platform.invokeMethod("checkPermission");
    print("SDFE + ${permissions}");

    if (permissions) {
      print("navigate");
      Get.put(HomeController());
      await Get.to(const HomeScreen());
      // Get.to(HomeScreen());
    } else {
      print("error");
      // TODO: // ERROR SCREEN
    }
  }
}
