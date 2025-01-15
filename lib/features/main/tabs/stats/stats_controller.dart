import 'package:curtail/features/main/tabs/app_control/action_controller.dart';
import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

class StatsController extends GetxController {
  final homeController = Get.find();

  var data = <FlSpot>[].obs;
  // var icons = <Uint8List>[].obs;
  var icons = <String>[].obs;

  var date = "".obs;

  ActionController appController = Get.find();

  @override
  void onInit() {
    print("stats controlle init");
    super.onInit();
    getUsageStats();
    getTodaysDate();
  }

  void getTodaysDate() {
    DateTime dateTime = DateTime.now();

    String formatedDate =
        "${DateFormat("EEEE").format(dateTime)}, ${DateFormat("MMMMd")}";

    date(formatedDate);
  }

  static const platform = MethodChannel('com.curtail.screenTime');

  Future<String> getScreenTime() async {
    print("get screen time");
    try {
      final String screenTime = await platform.invokeMethod('getScreenTime');
      print(screenTime);
      // print(intToTimeLeft(screenTime));
      // return screenTime;
      return screenTime;
    } on PlatformException catch (e) {
      print("Failed to get screen time: ${e.message}");
      return "0:0";
    }
  }

  intToTimeLeft(int value) {
    int totalScreenTimeMillis = 41747986; // Example milliseconds value

    // Convert milliseconds to hours and minutes
    int hours =
        totalScreenTimeMillis ~/ 3600000; // 1 hour = 3,600,000 milliseconds
    int minutes = (totalScreenTimeMillis % 3600000) ~/
        60000; // 1 minute = 60,000 milliseconds

    print("Hours: $hours, Minutes: $minutes");
  }

  void getUsageStats() async {
    data.value = <FlSpot>[];
    icons.value = <String>[];
    homeController.getAppUsage();
    // try {
    //   DateTime endDate = DateTime.now();
    //   DateTime startDate = endDate.subtract(const Duration(hours: 1));
    //   List<AppUsageInfo> infoList =
    //       await AppUsage().getAppUsage(startDate, endDate);

    //   print(infoList);
    //   print("@@");
    //   print(infoList.length);

    //   List<AppData> appData = appController.installedApps;

    //   for (var i = 0; i < infoList.length; i++) {
    //     if (infoList[i].usage.inHours.toDouble() + 1.2 > 0.1) {
    //       FlSpot spot =
    //           FlSpot(i.toDouble(), infoList[i].usage.inHours.toDouble() + 1.2);
    //       print("data add");

    //       data.add(spot);

    //       // for (AppData app in appData) {
    //       //   print(app.name);
    //       //   print("ME");
    //       //   print(infoList[i].appName);
    //       //   if (app.name == infoList[i].appName) {
    //       //     // icons.add(app.icon!);
    //       //     print("icons add");
    //       //     icons.add(app.name);
    //       //   }
    //       // }
    //     }
    //   }
    //   print(icons.length);
    //   print("!!");
    //   print(data.length);

    //   for (var i = 0; i < icons.length; i++) {
    //     print(icons[i]);
    //   }

    //   for (var i = 0; i < data.length; i++) {
    //     print("Data" + data[i].toString());
    //   }
    // } on AppUsageException catch (exception) {
    //   print(exception);
    // }
  }
}
