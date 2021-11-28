package com.example.cheaper.utilidades

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.cheaper.MainActivity
import com.example.cheaper.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val channelId = "notification_channel"
const val channelName = "com.example.cheaper"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(remoteMessage.notification != null){
            generarNotificacion(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
        }
    }

    fun generarNotificacion(titulo:String, mensaje: String){
        val intent = Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_ONE_SHOT)

        var builder:NotificationCompat.Builder = NotificationCompat.Builder(applicationContext,
            channelId)
            .setSmallIcon(R.drawable.ic_arrow_drop)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(titulo,mensaje))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0,builder.build())


    }

    @SuppressLint("RemoteViewLayout")
    fun getRemoteView(titulo: String, mensaje: String): RemoteViews? {
        val remoteViews = RemoteViews("com.example.cheaper",R.layout.notificacion)
        remoteViews.setTextViewText(R.id.notificacion_titulo, titulo)
        remoteViews.setImageViewResource(R.id.notificacion_logo, R.drawable.ic_arrow_drop)

        return remoteViews
    }
}