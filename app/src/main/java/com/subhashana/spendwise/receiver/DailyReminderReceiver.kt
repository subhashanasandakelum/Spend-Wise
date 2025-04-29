package com.subhashana.spendwise.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.subhashana.spendwise.notification.NotificationManager

class DailyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = NotificationManager(context)
        notificationManager.showDailyReminderNotification()
    }
} 