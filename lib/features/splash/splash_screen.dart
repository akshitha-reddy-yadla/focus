import 'package:flutter/material.dart';
import 'package:focus/features/splash/splash_controller.dart';
import 'package:get/get.dart';

class SplashScreen extends GetView<SplashController> {
  const SplashScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SizedBox(
          width: MediaQuery.sizeOf(context).width,
          height: MediaQuery.sizeOf(context).height,
          child: Column(
            children: [
              const Text("Splash screen"),
              ElevatedButton(
                  onPressed: () {
                    controller.askPermissions();
                  },
                  child: Text("sdf"))
            ],
          )),
    );
  }
}
