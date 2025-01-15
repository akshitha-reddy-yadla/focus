class AppInfo {
  AppInfo({
    required this.packageName,
    required this.usageTime,
    required this.appName,
    required this.appLogo,
  });

  final String? packageName;
  final String? usageTime;
  final String? appName;
  final String? appLogo;

  AppInfo copyWith({
    String? packageName,
    String? usageTime,
    String? appName,
    dynamic appLogo,
  }) {
    return AppInfo(
        packageName: packageName ?? this.packageName,
        usageTime: usageTime ?? this.usageTime,
        appName: appName ?? this.appName,
        appLogo: appLogo ?? this.appLogo);
  }

  factory AppInfo.fromJson(Map<String, dynamic> json) {
    return AppInfo(
        packageName: json["packageName"],
        usageTime: json["usageTime"],
        appName: json["appName"],
        appLogo: json["appLogo"]);
  }

  Map<String, dynamic> toJson() => {
        "packageName": packageName,
        "usageTime": usageTime,
        "appName": appName,
        "appLogo": appLogo
      };

  @override
  String toString() {
    return "$packageName, $usageTime, $appName, $appLogo";
  }
}
