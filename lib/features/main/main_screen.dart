// import 'package:curtail/features/main/tabs/app_control/action_tab.dart';
// import 'package:curtail/features/main/tabs/main_controller.dart';
// import 'package:curtail/features/main/tabs/stats/stats_screen.dart';
// import 'package:flutter/material.dart';
// import 'package:get/get.dart';

// class MainScreen extends GetView<MainController> {
//   const MainScreen({super.key});

//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       body: SizedBox(
//         width: MediaQuery.sizeOf(context).width,
//         height: MediaQuery.sizeOf(context).height,
//         child: Obx(() {
//           return switch (controller.selectedIndex()) {
//             0 => StatsScreen(),
//             1 => ActionTab(),
//             int() => throw UnimplementedError(),
//           };
//         }),
//       ),
//       bottomNavigationBar: Obx(
//         () => BottomNavigationBar(
//             onTap: (value) {
//               controller.onIndexUpdate(value);
//             },
//             currentIndex: controller.selectedIndex(),
//             items: controller.tabs
//                 .map((e) => BottomNavigationBarItem(
//                       icon: e.icon,
//                       activeIcon: e.selectedIcon,
//                       label: e.title,
//                     ))
//                 .toList()),
//       ),
//     );
//   }
// }
