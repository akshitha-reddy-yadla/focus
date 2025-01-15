import 'package:curtail/features/main/tabs/app_control/action_controller.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class TimerScreen extends GetView<ActionController> {
  const TimerScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Obx(() {
      return Container(
        color: Colors.white,
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              // Title
              const Text(
                'Set Overall Usage',
                style: TextStyle(fontSize: 16),
              ),
              const SizedBox(height: 20),

              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Expanded(
                    child: CupertinoPicker(
                      itemExtent: 32,
                      onSelectedItemChanged: (int index) {
                        controller.onHourChange(index);
                      },
                      children: List<Widget>.generate(24, (int index) {
                        return Center(
                            child: index > 1
                                ? Text('$index hrs')
                                : Text('$index hr'));
                      }),
                    ),
                  ),
                  Expanded(
                    child: CupertinoPicker(
                      itemExtent: 32,
                      onSelectedItemChanged: (int index) {
                        controller.onMinutesChange(index);
                      },
                      children: List<Widget>.generate(60, (int index) {
                        return Center(child: Text('$index min'));
                      }),
                    ),
                  ),
                ],
              ),

              const SizedBox(height: 20),

              // Display selected time
              Text(
                'Selected Time: ${controller.hours} hours and ${controller.minutes} minutes',
                style: const TextStyle(fontSize: 20),
              ),
              const SizedBox(height: 20),

              ElevatedButton(
                onPressed: () {
                  Get.back();
                },
                child: const Text("Done"),
              ),
              const SizedBox(height: 10),
            ],
          ),
        ),
      );
    });
  }
}
