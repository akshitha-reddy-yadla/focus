import 'package:flutter/material.dart';
import 'package:get/get.dart';

class MainController extends GetxController {
  final tabs = [
    (
      icon: const Icon(Icons.abc),
      selectedIcon: const Icon(Icons.abc_outlined),
      title: "Home",
    ),
    (
      icon: const Icon(Icons.access_time),
      selectedIcon: const Icon(Icons.access_time_filled_outlined),
      title: "Action",
    )
  ];

  final selectedIndex = 1.obs;

  void onIndexUpdate(int value) async {
    selectedIndex(value);
  }
}
