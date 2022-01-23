package com.s26462.widgetapp

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetManager.getInstance
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.s26462.widgetapp.utils.Constants
import android.graphics.drawable.BitmapDrawable

import android.graphics.Bitmap
import android.media.MediaPlayer
import android.provider.MediaStore
import androidx.core.graphics.drawable.toDrawable
import kotlin.coroutines.coroutineContext


/**
 * Implementation of App Widget functionality.
 */
class MyWidget : AppWidgetProvider() {
    private var requestCode = 0
    private val play = "play"
    private val pause = "pause"
    private val stop = "stop"
    private val change = "change"

    private var mp: MediaPlayer? = null
    var songs: MutableList<Int> = mutableListOf(R.raw.applause,R.raw.birds)
    var currentSong = 0
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, requestCode++,0)
        }
    }

    override fun onEnabled(context: Context) {
        Log.i("widget-app", "Pierwszy widget dodany.")
    }
    override fun onDisabled(context: Context) {
        Log.i("widget-app", "Ostatni widget usunięty.")
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        when(intent?.action) {
            "com.s26462.widgetapp.Action1" -> {
                val appWidgetId = intent?.getIntExtra("appWidgetId", 0)
                var image = intent?.getIntExtra("image", 0)
                if (image == 0) {
                    image = 1;
                } else {
                    image = 0;
                }
                if (appWidgetId != null && image != null) {
                    Toast.makeText(context, "Zmieniłeś obrazek", Toast.LENGTH_SHORT).show()
                    updateAppWidget(
                        context,
                        getInstance(context),
                        appWidgetId,
                        requestCode++,
                        image)
                    }
                }
            "com.s26462.widgetapp.Play" -> {
                player(context,play,songs[currentSong])
            }
            "com.s26462.widgetapp.Pause" -> {
                player(context,pause,songs[currentSong])
            }
            "com.s26462.widgetapp.Stop" -> {
                Log.e("MediaPlayer", "stop ID: ${mp!!.audioSessionId}")
                player(context,stop,songs[currentSong])
            }
            "com.s26462.widgetapp.Change" -> {
                player(context,change,songs[currentSong])
            }
        }
    }

    private fun player(context:Context, action: String, song: Int){

        when(action){
            play -> {
                if (mp == null){
                    mp = MediaPlayer.create(context,song)
                    Log.e("MediaPlayer", "ID: ${mp!!.audioSessionId}")
                }
                mp?.start()
                Log.e("MediaPlayer", "Duration: ${mp!!.duration/1000} seconds")
            }
            pause -> {
//                Log.e("MediaPlayer", "Pause ID: ${mp!!.audioSessionId}")
                if (mp != null) mp?.pause()
//                Log.e("MediaPlayer", "Pause at: ${mp!!.currentPosition/1000} seconds")
            }
            stop -> {
                Log.e("MediaPlayer", "stop ID: ${mp!!.audioSessionId}")
                if (mp != null) {
                    mp?.stop()
                    mp?.reset()
                    mp?.release()
                    mp = null
                }
            }
            change -> {
                if (song == songs[0]){
                    currentSong = 1
                } else {
                    currentSong = 0
                }
                player(context,play,songs[currentSong])
            }
        }
    }

private fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    requestCode: Int,
    image: Int
) {
    val views = RemoteViews(context.packageName, R.layout.my_widget)
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(Constants.WEBSITE_ADDRESS)
    val pendingIntent = PendingIntent.getActivity(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.btn_openWeb, pendingIntent)

    var images = intArrayOf(R.drawable.maluch,R.drawable.uno)
    if (image == 0) {
        views.setImageViewResource(R.id.iv_gallery, images[0])
    } else {
        views.setImageViewResource(R.id.iv_gallery, images[1])
    }


    val intent2 = Intent(context,MyWidget::class.java)
    intent2.action = "com.s26462.widgetapp.Action1"
    intent2.putExtra("appWidgetId",appWidgetId)
    intent2.putExtra("image",image)
    val pendingIntent2 = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent2,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    views.setOnClickPendingIntent(R.id.iv_gallery, pendingIntent2)

    val playMusicIntent = Intent(context,MyWidget::class.java)
    playMusicIntent.action = "com.s26462.widgetapp.Play"
    val pendingIntentPlayMusic = PendingIntent.getBroadcast(
        context,
        requestCode,
        playMusicIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    views.setOnClickPendingIntent(R.id.iv_play,pendingIntentPlayMusic)

    val pauseMusicIntent = Intent(context,MyWidget::class.java)
    pauseMusicIntent.action = "com.s26462.widgetapp.Pause"
    val pendingIntentPauseMusic = PendingIntent.getBroadcast(
        context,
        requestCode,
        pauseMusicIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    views.setOnClickPendingIntent(R.id.iv_pause,pendingIntentPauseMusic)

    val stopMusicIntent = Intent(context,MyWidget::class.java)
    stopMusicIntent.action = "com.s26462.widgetapp.Stop"
    val pendingIntentStopMusic = PendingIntent.getBroadcast(
        context,
        requestCode,
        stopMusicIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    views.setOnClickPendingIntent(R.id.iv_stop,pendingIntentStopMusic)

    val changeMusicIntent = Intent(context,MyWidget::class.java)
    changeMusicIntent.action = "com.s26462.widgetapp.Change"
    val pendingIntentChangeMusic = PendingIntent.getBroadcast(
        context,
        requestCode,
        changeMusicIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    views.setOnClickPendingIntent(R.id.iv_change,pendingIntentChangeMusic)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}
}
