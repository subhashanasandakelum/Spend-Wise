package com.subhashana.spendwise.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.subhashana.spendwise.MainActivity
import com.subhashana.spendwise.R

class NotificationManager(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID_DAILY_REMINDER = "daily_reminder_channel"
        const val CHANNEL_ID_BUDGET_ALERT = "budget_alert_channel"
        const val NOTIFICATION_ID_DAILY_REMINDER = 1001
        const val NOTIFICATION_ID_BUDGET_ALERT = 1002
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dailyReminderChannel = NotificationChannel(
                CHANNEL_ID_DAILY_REMINDER,
                "Daily Expense Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminds you to record your daily expenses"
                enableLights(true)
                enableVibration(true)
            }

            val budgetAlertChannel = NotificationChannel(
                CHANNEL_ID_BUDGET_ALERT,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when you are approaching or exceeding your budget"
                enableLights(true)
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(dailyReminderChannel)
            notificationManager.createNotificationChannel(budgetAlertChannel)
        }
    }

    fun showDailyReminderNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_DAILY_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.daily_reminder))
            .setContentText(context.getString(R.string.daily_reminder_message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID_DAILY_REMINDER, notification)
    }

    fun showBudgetAlert(currentExpenses: Double, monthlyBudget: Double) {
        val percentage = (currentExpenses / monthlyBudget) * 100
        val message = when {
            percentage >= 100 -> context.getString(R.string.budget_exceeded)
            percentage >= 80 -> context.getString(R.string.budget_approaching)
            else -> return // Don't show notification if under 80%
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_BUDGET_ALERT)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.budget_alert))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID_BUDGET_ALERT, notification)
    }
} 