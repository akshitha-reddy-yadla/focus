import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:focus/data/app_info.dart';
import 'package:focus/features/home/home_controller.dart';
import 'package:focus/features/popUp/pop_up_controller.dart';
import 'package:focus/global/spacer.dart';
import 'package:get/get.dart';

class HomeScreen extends GetView<HomeController> {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: SingleChildScrollView(
          child: Container(
            width: MediaQuery.sizeOf(context).width,
            height: MediaQuery.sizeOf(context).height,
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Container(
                  width: MediaQuery.sizeOf(context).width,
                  decoration: const BoxDecoration(
                    color: Colors.brown,
                    borderRadius: BorderRadius.all(
                      Radius.circular(16.0),
                    ),
                  ),
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        "Today's screen time",
                        style: TextStyle(
                            fontWeight: FontWeight.w300,
                            fontSize: 14,
                            color: Colors.amber[100]),
                      ),
                      Obx(() {
                        return Text(
                          controller.screenTime.value,
                          style: TextStyle(
                            color: Colors.amber[50],
                            fontSize: 28,
                            fontWeight: FontWeight.w500,
                          ),
                        );
                      }),
                    ],
                  ),
                ),
                const VSpacer(height: 30),
                Expanded(child: Obx(() {
                  return ListView.separated(
                      scrollDirection: Axis.vertical,
                      separatorBuilder: (context, index) => const Divider(),
                      itemCount: controller.installedApps.length,
                      itemBuilder: (context, index) {
                        AppInfo item = controller.installedApps[index];
                        return ListTile(
                          contentPadding: const EdgeInsets.symmetric(
                              vertical: 6.0, horizontal: 16.0),
                          dense: true,
                          leading: Container(
                            width: 40,
                            height: 40,
                            child: Container(
                              decoration: BoxDecoration(
                                color: Colors.grey.shade100,
                                borderRadius: BorderRadius.circular(30),
                              ),
                              width: 38,
                              height: 38,
                              child: controller.installedApps[index].appIcon !=
                                      null
                                  ? Image.memory(base64Decode(controller
                                      .installedApps[index].appIcon!
                                      .replaceAll(RegExp(r'[\s]'), '')))
                                  : Container(),
                            ),
                          ),
                          subtitle: Row(
                            children: [
                              Text(
                                controller.installedApps[index].usageTime
                                    .toString(),
                                style: const TextStyle(
                                  fontSize: 12,
                                  color: Colors.grey,
                                ),
                              ),
                            ],
                          ),
                          title: Text(controller.installedApps[index].appName!),
                        );
                      });
                }))
              ],
            ),
          ),
        ),
      ),
      floatingActionButton: FloatingActionButton(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(30)),
        foregroundColor: Colors.brown.shade700,
        backgroundColor: Colors.brown.shade200,
        onPressed: () {
          Get.put(PopUpController());

          controller.openBottomSheet(context);
        },
        child: const Icon(Icons.app_blocking),
      ),
    );
  }
}
