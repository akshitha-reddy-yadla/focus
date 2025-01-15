import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class MainActivity : FlutterActivity() {

    private val CHANNEL = "com.example.curtail"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->
            if (call.method == "getScreenTimeUsage") {
                result.success(getStats())
            } else {
                result.notImplemented()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStats(): Map<String, String> {
        val currentTime = System.currentTimeMillis()

        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy HH:mm:ss")
        val localDate = LocalDate.now()   // your current date time
        val startOfDay: LocalDateTime = localDate.atStartOfDay() // date time at start of the date
        val startTime = startOfDay.atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli() // start time to timestamp
        Log.d("Date:", "start date $startTime")
        Log.d("Date:", "start date parsed ${startOfDay.format(dateFormatter)}")

        val timeOneHourAgo = currentTime - (60 * 60 * 1000)

        val total = getScreenUsage(currentTime, startTime);
        val lastHour = getScreenUsage(currentTime, timeOneHourAgo)
        return mapOf(
            "lastHour" to lastHour,
            "total" to total
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getScreenUsage(currentTime: Long, startTime: Long): String {
       val usageStatsManager = applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager


        val stats: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTime, currentTime
        )

        var totalScreenTime: Long = 0

        var hours = 0;
        var minutes = 0;

        for(usageStats in stats) {
            totalScreenTime += usageStats.totalTimeInForeground

             hours = (totalScreenTime / 3600000).toInt()
             minutes = ((totalScreenTime % 3600000) / 60000).toInt()
          }

        return "${formatDigits(hours)}:${formatDigits(minutes)}";
    }

    private fun formatDigits(num: Int): String {
        return if (abs(num).toString().trim().length == 2) {
            num.toString()
        } else {
            "0$num"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
