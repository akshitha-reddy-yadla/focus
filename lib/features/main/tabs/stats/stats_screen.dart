import 'package:curtail/features/main/tabs/stats/stats_controller.dart';
import 'package:curtail/features/main/tabs/stats/widgets/line_chart.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class StatsScreen extends GetView<StatsController> {
  const StatsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.start,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        const Text("stats"),
        Row(
          children: [
            const Text("SCREEN TIME"),
            Container(
              child: Obx(() {
                return Text(controller.date.value);
              }),
            )
          ],
        ),
        // SplineChartDemo(),
        Obx(() {
          print(controller.data.length);
          print("^^");
          print(controller.icons.length);
          return controller.data.isEmpty
              ? const CircularProgressIndicator()
              : LineChartSample2(
                  spots: controller.data,
                  icons: controller.icons,
                );
        }),
        ElevatedButton(
          onPressed: () {
            controller.getScreenTime();
          },
          child: const Text("get stats"),
        ),
      ],
    );
  }
}
