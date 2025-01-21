import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/widgets.dart';
import 'package:focus/data/app_info.dart';
import 'package:focus/features/popUp/pop_up_controller.dart';
import 'package:focus/global/spacer.dart';
import 'package:get/get.dart';

class PopUpScreen extends GetView<PopUpController> {
  const PopUpScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return BottomSheet(
      onClosing: () {
        Get.back();
      },
      enableDrag: true,
      showDragHandle: true,
      builder: (context) {
        return Stack(
          children: [
            SingleChildScrollView(
              child: Container(
                padding: const EdgeInsets.all(32),
                child: Column(
                  children: [
                    _buildContainer(
                      controller.droppedItems,
                      controller.moveItemToDropped,
                      controller.removeItemFromDropped,
                      isOriginalList: false,
                    ),
                    const VSpacer(height: 20),
                    _buildContainer(
                      controller.installedApps,
                      controller.moveItemToDropped,
                      controller.removeItemFromDropped,
                      isOriginalList: true,
                    ),
                  ],
                ),
              ),
            ),
            Align(
              alignment: Alignment.bottomCenter,
              child: ElevatedButton(
                onPressed: () {
                  print("Clicked");
                  controller.onContinue();
                },
                child: const Text(
                  'Block selected apps',
                ),
              ),
            )
          ],
        );
      },
    );
  }

  Widget _buildContainer(RxList<AppInfo> items,
      Function(AppInfo) moveItemCallback, Function(AppInfo) removeItemCallback,
      {bool isOriginalList = true}) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const SizedBox(height: 10),
        DragTarget<AppInfo>(
          onAccept: (data) {
            moveItemCallback(data);
          },
          builder: (context, candidateData, rejectedData) {
            return Visibility(
              visible: !isOriginalList,
              child: Container(
                height: 100,
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  border: Border.all(color: Colors.black, width: 2),
                  color: Colors.grey[200],
                ),
                child: candidateData.isEmpty
                    ? const Center(child: Text('Drop Here'))
                    : const Center(child: Text('Can Drop Here')),
              ),
            );
          },
        ),
        Obx(() {
          return Visibility(
            visible: items.isEmpty && !isOriginalList,
            child: Container(
              width: Get.width,
              height: 100,
              alignment: Alignment.center,
              child: const Text(
                "No apps are blocked yet. Drag and drop apps here to prevent them from opening.",
                style: TextStyle(color: Colors.green),
                textAlign: TextAlign.center,
              ),
            ),
          );
        }),
        Obx(() {
          return ConstrainedBox(
            constraints: const BoxConstraints(
              maxHeight: double.infinity,
            ),
            child: ListView.builder(
              shrinkWrap: true,
              itemCount: items.length,
              itemBuilder: (context, index) {
                return isOriginalList
                    ? Draggable<AppInfo>(
                        data: items[index],
                        feedback: Material(
                          color: Colors.transparent,
                          child: _buildDraggableItem(
                              items[index].appName.toString()),
                        ),
                        childWhenDragging: _buildDraggableItem(
                            items[index].appName.toString(),
                            isDragging: true),
                        child: _buildDraggableItem(
                            items[index].appName.toString()),
                      )
                    : ListTile(
                        title: _buildDraggableItem(
                            items[index].appName.toString()),
                        trailing: IconButton(
                          icon: Icon(Icons.delete, color: Colors.red[200]),
                          onPressed: () {
                            removeItemCallback(items[index]);
                          },
                        ),
                      );
              },
            ),
          );
        }),
      ],
    );
  }

  Widget _buildDraggableItem(String name, {bool isDragging = false}) {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 16),
      margin: const EdgeInsets.symmetric(vertical: 5),
      decoration: BoxDecoration(
        color: isDragging ? Colors.grey[300] : Colors.blue[100],
        borderRadius: BorderRadius.circular(8),
      ),
      child: Text(
        name,
        style: const TextStyle(fontSize: 16),
      ),
    );
  }
}
