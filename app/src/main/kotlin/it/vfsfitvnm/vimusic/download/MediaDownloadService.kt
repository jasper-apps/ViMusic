package it.vfsfitvnm.vimusic.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.Scheduler
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.utils.globalCache
import java.util.concurrent.Executors


private const val DOWNLOAD_NOTIFICATION_ID = 137834
private const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "85874"
private const val DOWNLOAD_NOTIFICATION_CHANNEL_NAME = "Downloading"

class MediaDownloadService : DownloadService(DOWNLOAD_NOTIFICATION_ID) {

    override fun getScheduler(): Scheduler? = null

    override fun getDownloadManager(): DownloadManager {
        val databaseProvider = StandaloneDatabaseProvider(this)
        val executor = Executors.newSingleThreadExecutor()

        val dataSourceFactory = DefaultHttpDataSource.Factory()

        Log.i("info23", "providing download manager")

        return DownloadManager(
            this,
            databaseProvider,
            globalCache,
            dataSourceFactory,
            executor
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int
    ): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DOWNLOAD_NOTIFICATION_CHANNEL_ID,
                DOWNLOAD_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val helper = DownloadNotificationHelper(this, DOWNLOAD_NOTIFICATION_CHANNEL_ID)
        val percent: Int = when (val size = downloads.size) {
            0 -> 0
            else -> downloads
                .map { it.percentDownloaded }
                .sum()
                .toInt() / size
        }

        val notification = when {
            notMetRequirements > 0 -> helper.buildDownloadFailedNotification(
                this,
                R.drawable.download,
                null,
                "Download failed. Error code: $notMetRequirements"
            )
            percent > 99 -> helper.buildDownloadCompletedNotification(
                this,
                R.drawable.download,
                null,
                "Done!"
            )
            else -> helper.buildProgressNotification(
                this,
                R.drawable.download,
                null,
                "Progress: $percent%",
                downloads,
                notMetRequirements
            )
        }
        return notification
    }
}