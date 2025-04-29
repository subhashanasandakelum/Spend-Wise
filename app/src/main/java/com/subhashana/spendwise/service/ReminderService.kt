package com.subhashana.spendwise.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.subhashana.spendwise.notification.NotificationManager
import com.subhashana.spendwise.receiver.DailyReminderReceiver
import java.util.Calendar

class ReminderService : Service() {
    private lateinit var alarmManager: AlarmManager
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        notificationManager = NotificationManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_REMINDER -> scheduleReminder()
            ACTION_STOP_REMINDER -> cancelReminder()
        }
        return START_NOT_STICKY
    }

    private fun scheduleReminder() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20) // Set reminder for 8 PM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            
            // If current time is past 8 PM, schedule for next day
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(this, DailyReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun cancelReminder() {
        val intent = Intent(this, DailyReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START_REMINDER = "com.subhashana.spendwise.START_REMINDER"
        const val ACTION_STOP_REMINDER = "com.subhashana.spendwise.STOP_REMINDER"
        private const val REMINDER_REQUEST_CODE = 123
    }
} 