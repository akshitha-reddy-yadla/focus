import 'dart:convert';

import 'package:curtail/features/main/home/home_controller.dart';
import 'package:curtail/features/main/tabs/app_control/action_controller.dart';
import 'package:curtail/features/main/tabs/app_control/action_tab.dart';
import 'package:curtail/global/spacer.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class HomeScreen extends GetView<HomeController> {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      body: SafeArea(
        child: Container(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Text(
                    "SCREEN TIME",
                    style: TextStyle(
                        fontWeight: FontWeight.w700,
                        color: Colors.grey.shade700),
                  ),
                  Container(
                    padding: const EdgeInsets.all(6),
                    decoration: BoxDecoration(
                        border: Border.all(color: Colors.grey.shade300),
                        borderRadius: BorderRadius.circular(30)),
                    child: Obx(
                      () {
                        return Text(
                          controller.date.value,
                          style: const TextStyle(fontSize: 12.0),
                        );
                      },
                    ),
                  )
                ],
              ),
              const VSpacer(height: 16),
              Obx(() {
                return Padding(
                  padding: const EdgeInsets.all(16),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      TextWithLabel(
                        text: controller.screenTime.value,
                        label: "Today",
                        fontSize: 24,
                      ),
                      TextWithLabel(
                        text: controller.screenTimeinLastOneHour.value,
                        label: "Last hour",
                        fontSize: 18,
                      ),
                      TextWithLabel(
                        text: controller.listOfApps().length.toString(),
                        label: "Phone pickups",
                        fontSize: 18,
                      ),
                    ],
                  ),
                );
              }),
              Expanded(
                child: Obx(
                  () {
                    return controller.listOfApps.isEmpty
                        ? const Center(
                            child: CircularProgressIndicator(),
                          )
                        : ListView.separated(
                            itemBuilder: (context, index) {
                              return ListTile(
                                title: Column(
                                  mainAxisAlignment: MainAxisAlignment.start,
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    CircleAvatar(
                                      backgroundColor: Colors.grey.shade200,
                                      child: Image.memory(
                                        base64Decode(controller
                                            .listOfApps[index].appLogo!),
                                      ),
                                    ),
                                    const VSpacer(height: 5),
                                    Text(
                                      controller.listOfApps[index].appName
                                          .toString(),
                                      style: TextStyle(
                                          fontSize: 12,
                                          fontWeight: FontWeight.w300,
                                          color: Colors.grey.shade800),
                                    )
                                  ],
                                ),
                                trailing: Text(controller
                                    .listOfApps[index].usageTime
                                    .toString()),
                              );
                            },
                            separatorBuilder: (context, index) =>
                                const Divider(),
                            itemCount: controller.listOfApps.length);
                  },
                ),
              ),
            ],
          ),
        ),
      ),
      floatingActionButton: FloatingActionButton(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(30)),
        foregroundColor: Colors.brown.shade700,
        backgroundColor: Colors.brown.shade200,
        onPressed: () {
          Get.put(ActionController());
          showModalBottomSheet<void>(
            context: context,
            builder: (BuildContext context) {
              return const SizedBox(
                child: ActionTab(),
              );
            },
          );
        },
        child: const Icon(Icons.app_blocking),
      ),
    );
  }
}

class TextWithLabel extends StatelessWidget {
  const TextWithLabel(
      {super.key,
      required this.text,
      required this.label,
      required this.fontSize});

  final String text;
  final String label;
  final double fontSize;

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.start,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          text,
          style: TextStyle(
              color: Colors.black,
              fontWeight: FontWeight.bold,
              fontSize: fontSize),
        ),
        Text(
          label,
          style: const TextStyle(
            fontSize: 12,
            fontWeight: FontWeight.w800,
            color: Colors.black,
          ),
        )
      ],
    );
  }
}
