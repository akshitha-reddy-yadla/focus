import 'dart:convert';

import 'package:curtail/features/main/tabs/app_control/action_controller.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class ActionTab extends GetView<ActionController> {
  const ActionTab({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        padding: const EdgeInsets.all(16.0),
        width: MediaQuery.sizeOf(context).width,
        height: MediaQuery.sizeOf(context).height,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            const Text("Select apps to block"),
            Expanded(
              child: Obx(
                () {
                  return controller.isLoading.value
                      ? const Center(
                          child: CircularProgressIndicator(),
                        )
                      : ListView.separated(
                          separatorBuilder: (context, index) => const Divider(),
                          itemCount: controller.installedApps.length,
                          itemBuilder: (context, index) {
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
                                  child: controller.installedApps[index].icon !=
                                              null &&
                                          controller.installedApps[index].icon!
                                              .isNotEmpty
                                      ? Image.memory(base64Decode(controller
                                          .installedApps[index].icon!))
                                      : Container(),
                                ),
                              ),
                              trailing: Switch(
                                value: controller
                                    .installedApps[index].isRestricted,
                                onChanged: (bool value) {
                                  controller.onToggle(
                                      controller.installedApps[index].id);
                                },
                              ),
                              subtitle: GestureDetector(
                                onTap: () {
                                  controller.onSelectTimer();
                                },
                                child: const Row(
                                  children: [
                                    Text(
                                      "Set time limit",
                                      style: TextStyle(
                                        fontSize: 12,
                                        color: Colors.grey,
                                      ),
                                    ),
                                    SizedBox(
                                      width: 2,
                                    ),
                                    Icon(
                                      Icons.keyboard_arrow_down_outlined,
                                      color: Colors.grey,
                                      size: 14,
                                    )
                                  ],
                                ),
                              ),
                              title: Text(controller.installedApps[index].name),
                            );
                          },
                        );
                },
              ),
            ),
            const SizedBox(
              height: 20,
            ),
            ElevatedButton(
              onPressed: () {
                controller.onDone();
              },
              child: const Text('Done'),
            ),
          ],
        ),
      ),
    );
  }
}
