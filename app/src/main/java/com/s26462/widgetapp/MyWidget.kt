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
import androidx.core.graphics.drawable.toDrawable


/**
 * Implementation of App Widget functionality.
 */
class MyWidget : AppWidgetProvider() {
    private var requestCode = 0
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
        if(intent?.action.equals("com.s26462.widgetapp.Action1")) {

            val appWidgetId = intent?.getIntExtra("appWidgetId",0)
            var image = intent?.getIntExtra("image",0)
            if (image == 0) {
                image = 1;
            } else {
                image = 0;
            }
            if (appWidgetId != null && image != null) {
                Toast.makeText(context, "Zmieniłeś obrazek", Toast.LENGTH_SHORT).show()
                updateAppWidget(context, getInstance(context), appWidgetId, requestCode++,image)
            }
        }
        super.onReceive(context, intent)
    }

}

internal fun updateAppWidget(
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
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.iv_gallery, pendingIntent2)

    /*
    * audio
    */
    val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()
    val soundPool = SoundPool.Builder()
        .setAudioAttributes(audioAttributes)
        .setMaxStreams(5)
        .build()
    soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
        Log.i("media_app", "Loaded sample with id: $sampleId, status: $status")
    }
    val prio = 1 // 0 = min priorytet
    val sampleId = soundPool.load(context, R.raw.applause, prio)
    val leftVol = 0.85F //0.0 - 1.0
    val rightVol = 0.9F //0.0 - 1.1
    val loop = 0 //-1 nieskończone zapętlenie, 0 bez zapętlenia, 3 potrójne zapętlenie
    val speed = 1F //0.5 - 2.0
    val streamId = soundPool.play(sampleId, leftVol, rightVol, prio, loop, speed)
    soundPool.pause(streamId)
    soundPool.resume(streamId)
    soundPool.stop(streamId)
    soundPool.release()

    appWidgetManager.updateAppWidget(appWidgetId, views)
}
