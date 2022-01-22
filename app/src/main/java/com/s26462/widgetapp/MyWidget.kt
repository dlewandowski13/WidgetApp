package com.s26462.widgetapp

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.s26462.widgetapp.utils.Constants

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
            updateAppWidget(context, appWidgetManager, appWidgetId, requestCode++)
        }
    }

    override fun onEnabled(context: Context) {
        Log.i("widget-app", "Pierwszy widget dodany.")
    }
    override fun onDisabled(context: Context) {
        Log.i("widget-app", "Ostatni widget usunięty.")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if(intent?.action.equals("com.s26462.widgetapp.Action1"))
            Toast.makeText(context, "Action1", Toast.LENGTH_SHORT).show()
    }

}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    requestCode: Int
) {
    val widgetText = context.getString(R.string.appwidget_text)
    val views = RemoteViews(context.packageName, R.layout.my_widget)
    views.setTextViewText(R.id.appwidget_text, widgetText)
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(Constants.WEBSITE_ADDRESS)
    val pendingIntent = PendingIntent.getActivity(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    views.setOnClickPendingIntent(R.id.btn_openWeb, pendingIntent)
    val intent2 = Intent(Intent.ACTION_VIEW)
    intent2.action = "com.s26462.widgetapp.Action1"
    val pendingIntent2 = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent2,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    views.setOnClickPendingIntent(R.id.button2, pendingIntent2)
    appWidgetManager.updateAppWidget(appWidgetId, views)
}
