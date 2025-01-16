import 'package:flutter/services.dart';
import 'package:get/get.dart';

class HomeController extends GetxController {
  @override
  void onInit() {
    super.onInit();
  }

  void getPhoneStats() async {
    const platform = MethodChannel("com.example.focus");
    final usage = await platform.invokeMethod("getScreenTimeUsage");

    print("result");

    print(usage);
  }
}
