// import 'package:installed_apps/app_info.dart';

class AppData {
  String id;
  String name;
  String? icon;
  String packageName;
  String versionName;
  int versionCode;
  // BuiltWith builtWith;
  int installedTimestamp;
  bool isRestricted;

  AppData(
      {required this.id,
      required this.name,
      required this.icon,
      required this.packageName,
      required this.versionName,
      required this.versionCode,
      // required this.builtWith,
      required this.installedTimestamp,
      required this.isRestricted});

  factory AppData.create(dynamic data) {
    return AppData(
        id: data["id"],
        name: data["name"],
        icon: data["icon"],
        packageName: data["package_name"],
        versionName: data["version_name"] ?? "1.0.0",
        versionCode: data["version_code"] ?? 1,
        // builtWith: parseBuiltWith(data["built_with"]),
        installedTimestamp: data["installed_timestamp"] ?? 0,
        isRestricted: data["is_restricted"] ?? false);
  }

  String getVersionInfo() {
    return "$versionName ($versionCode)";
  }

  String returnData() {
    return "$name, $icon, $packageName, $versionName, $versionCode, $installedTimestamp";
  }

  static List<AppData> parseList(dynamic apps) {
    if (apps == null || apps is! List || apps.isEmpty) return [];
    final List<AppData> appInfoList = apps
        .where((element) =>
            element is Map &&
            element.containsKey("name") &&
            element.containsKey("package_name"))
        .map((app) => AppData.create(app))
        .toList();
    appInfoList.sort((a, b) => a.name.compareTo(b.name));
    return appInfoList;
  }

  // static BuiltWith parseBuiltWith(String? builtWithRaw) {
  //   if (builtWithRaw == "flutter") {
  //     return BuiltWith.flutter;
  //   } else if (builtWithRaw == "react_native") {
  //     return BuiltWith.react_native;
  //   } else if (builtWithRaw == "xamarin") {
  //     return BuiltWith.xamarin;
  //   } else if (builtWithRaw == "ionic") {
  //     return BuiltWith.ionic;
  //   }
  //   return BuiltWith.native_or_others;
  // }
}
