import 'package:flutter/material.dart';
import 'package:get/get.dart';

class SplashScreen extends GetView {
  const SplashScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SizedBox(
        width: MediaQuery.sizeOf(context).width,
        height: MediaQuery.sizeOf(context).height,
        child: const Text("Splash screen"),
      ),
    );
  }
}
