import 'package:focus/data/app_info.dart';
import 'package:focus/features/home/home_controller.dart';
import 'package:get/get.dart';

class PopUpController extends GetxController {
  final homeController = Get.find<HomeController>();

  final installedApps = <AppInfo>[].obs;

  var droppedItems = <AppInfo>[].obs;

  @override
  void onInit() {
    super.onInit();
    installedApps.value = homeController.installedApps;
  }

  void moveItemToDropped(AppInfo item) {
    installedApps.remove(item);
    droppedItems.add(item);
  }

  void removeItemFromDropped(AppInfo item) {
    droppedItems.remove(item);
    installedApps.add(item);
  }
}
