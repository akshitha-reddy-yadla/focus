import 'package:flutter/material.dart';
import 'package:focus/features/home/home_controller.dart';
import 'package:get/get.dart';

class HomeScreen extends GetView<HomeController> {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Container(
          width: MediaQuery.sizeOf(context).width,
          height: MediaQuery.sizeOf(context).height,
          child: const Column(
            children: [
              Text("Home"),
            ],
          ),
        ),
      ),
      floatingActionButton: FloatingActionButton(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(30)),
        foregroundColor: Colors.brown.shade700,
        backgroundColor: Colors.brown.shade200,
        onPressed: () {
          // Get.put(SplashController());
          // Get.to(SplashScreen());
          controller.getPhoneStats();
          showModalBottomSheet<void>(
            context: context,
            builder: (BuildContext context) {
              return const SizedBox(
                  // child: ActionTab(),
                  );
            },
          );
        },
        child: const Icon(Icons.app_blocking),
      ),
    );
  }
}
