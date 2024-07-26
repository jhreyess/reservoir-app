package com.jhreyess.reservoir

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.jhreyess.reservoir.ui.theme.ReservoirTheme
import com.jhreyess.reservoir.util.minutesUntilTarget
import com.jhreyess.reservoir.workers.CollectorWorker
import com.jhreyess.reservoir.workers.PollDataWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val hoursLeft = minutesUntilTarget(13, 30)

        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        val pollRequest = PeriodicWorkRequestBuilder<PollDataWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraint)
            .addTag("dataworker")
            .setInitialDelay(hoursLeft, TimeUnit.MINUTES)
            .build()
        val collectorRequest = PeriodicWorkRequestBuilder<CollectorWorker>(5, TimeUnit.DAYS)
            .addTag("collectorworker")
            .setInitialDelay(5, TimeUnit.DAYS)
            .build()
        val workManager = WorkManager.getInstance(applicationContext)

        workManager
            .enqueueUniquePeriodicWork(
                "dataworker",
                ExistingPeriodicWorkPolicy.KEEP,
                pollRequest
            )
        workManager
            .enqueueUniquePeriodicWork(
            "collectorworker",
                ExistingPeriodicWorkPolicy.KEEP,
                collectorRequest
            )
        setContent {
            ReservoirTheme {
                // A surface container using the 'background' color from the theme

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        0
                    )
                    /*ContextCompat.checkSelfPermission(
                        LocalContext.current,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED*/
                }


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}