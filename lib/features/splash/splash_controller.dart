import 'package:flutter/services.dart';
import 'package:focus/features/home/home_controller.dart';
import 'package:focus/features/home/home_screen.dart';
import 'package:focus/features/popUp/pop_up_controller.dart';
import 'package:get/get.dart';

class SplashController extends GetxController {
  final platform = const MethodChannel("com.example.focus");

  @override
  void onInit() {
    super.onInit();
    _checkPermissions();
  }

  void _checkPermissions() async {
    final bool permissions = await platform.invokeMethod("checkPermission");
    print("SDFE + ${permissions}");

    if (permissions) {
      print("navigate");
      Get.put(HomeController());
      Get.put(PopUpController());
      Future.delayed(
        const Duration(seconds: 3),
        () => 100,
      ).then((value) {
        Get.to(const HomeScreen());
        print('The value is $value.'); // Prints later, after 3 seconds
      });

      // Get.to(HomeScreen());
    } else {
      print("error");
      // TODO: // ERROR SCREEN
    }
  }

  askPermissions() async {
    final value = await platform.invokeListMethod("grantPermission");
  }
}
