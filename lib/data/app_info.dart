import 'package:get/get.dart';

class AppInfo {
  AppInfo(
      {required this.id,
      required this.packageName,
      required this.usageTime,
      required this.appName,
      required this.appIcon,
      this.isRestricted});

  final String? id;
  final String? packageName;
  final String? usageTime;
  final String? appName;
  final String? appIcon;
  RxBool? isRestricted = false.obs;

  AppInfo copyWith({
    String? id,
    String? packageName,
    String? usageTime,
    String? appName,
    String? appIcon,
    bool? isRestricted,
  }) {
    return AppInfo(
      id: id,
      packageName: packageName ?? this.packageName,
      usageTime: usageTime ?? this.usageTime,
      appName: appName ?? this.appName,
      appIcon: appIcon ?? this.appIcon,
    );
  }

  factory AppInfo.fromJson(Map<String, dynamic> json) {
    return AppInfo(
        id: json["id"],
        packageName: json["packageName"],
        usageTime: convertMillisecondsToTime(json["usageTime"]),
        appName: json["appName"],
        appIcon: json["appIcon"],
        isRestricted: json["isRestricted"] ?? false.obs);
  }

  factory AppInfo.fromMap(Map<String, dynamic> map) {
    return AppInfo(
      id: map["id"],
      packageName: map["packageName"],
      usageTime: convertMillisecondsToTime(map["usageTime"]),
      appIcon: map["appIcon"],
      appName: map["appName"],
      isRestricted: map["isRestricted"] ?? false,
    );
  }

  Map<String, dynamic> toJson() => {
        "id": id,
        "packageName": packageName,
        "usageTime": usageTime,
        "appName": appName,
        "appIcon": appIcon,
        "isRestricted": isRestricted
      };

  @override
  String toString() {
    return "$id, $packageName, $usageTime, $appName, $appIcon, $isRestricted";
  }
}

String convertMillisecondsToTime(int milliseconds) {
  // Convert milliseconds to seconds
  int totalSeconds = (milliseconds / 1000).round();

  // Calculate hours and minutes
  int hours = totalSeconds ~/ 3600; // Divide by 3600 seconds (1 hour)
  int minutes = (totalSeconds % 3600) ~/
      60; // Get remainder of hours and divide by 60 for minutes

  // Return formatted string in "hh:mm" format
  return '${hours.toString().padLeft(2, '0')}:${minutes.toString().padLeft(2, '0')}';
}
