package com.subhashana.spendwise.backup

import android.content.Context
import android.util.Log
import com.subhashana.spendwise.data.TransactionManager
import com.subhashana.spendwise.model.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BackupManager(private val context: Context) {
    private val gson = Gson()
    private val backupDir: File
        get() = File(context.filesDir, "backups").apply { mkdirs() }

    fun createBackup(transactionManager: TransactionManager): String? {
        return try {
            val backupData = BackupData(
                transactions = transactionManager.getTransactions(),
                monthlyBudget = transactionManager.getMonthlyBudget()
            )

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val backupFile = File(backupDir, "backup_$timestamp.json")
            backupFile.writeText(gson.toJson(backupData))

            backupFile.absolutePath
        } catch (e: Exception) {
            Log.e("BackupManager", "Error creating backup", e)
            null
        }
    }

    fun restoreBackup(backupPath: String): BackupData? {
        return try {
            val backupFile = File(backupPath)
            if (!backupFile.exists()) {
                Log.e("BackupManager", "Backup file does not exist: $backupPath")
                return null
            }

            val type = object : TypeToken<BackupData>() {}.type
            gson.fromJson(backupFile.readText(), type)
        } catch (e: Exception) {
            Log.e("BackupManager", "Error restoring backup", e)
            null
        }
    }

    fun getBackupFiles(): List<File> {
        return try {
            backupDir.listFiles()?.filter { it.name.startsWith("backup_") }?.sortedByDescending { it.lastModified() } ?: emptyList()
        } catch (e: Exception) {
            Log.e("BackupManager", "Error getting backup files", e)
            emptyList()
        }
    }

    fun deleteBackup(backupPath: String): Boolean {
        return try {
            val backupFile = File(backupPath)
            if (backupFile.exists()) {
                backupFile.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("BackupManager", "Error deleting backup", e)
            false
        }
    }

    fun clearAllBackups(): Boolean {
        return try {
            val files = backupDir.listFiles()?.filter { it.name.startsWith("backup_") }
            var success = true
            files?.forEach { file ->
                if (!file.delete()) {
                    success = false
                }
            }
            success
        } catch (e: Exception) {
            Log.e("BackupManager", "Error clearing all backups", e)
            false
        }
    }

    data class BackupData(
        val transactions: List<Transaction>,
        val monthlyBudget: Double
    )
} 