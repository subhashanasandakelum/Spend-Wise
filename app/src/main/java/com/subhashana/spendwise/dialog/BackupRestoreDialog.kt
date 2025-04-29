package com.subhashana.spendwise.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.subhashana.spendwise.MainActivity
import com.subhashana.spendwise.R
import com.subhashana.spendwise.backup.BackupManager
import java.text.SimpleDateFormat
import java.util.Locale

class BackupRestoreDialog : DialogFragment() {
    private var onBackupCreated: ((String) -> Unit)? = null
    private var onBackupRestored: ((BackupManager.BackupData) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_backup_restore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCreateBackup = view.findViewById<Button>(R.id.btnCreateBackup)
        val btnClearBackups = view.findViewById<Button>(R.id.btnClearBackups)
        val listViewBackups = view.findViewById<ListView>(R.id.listViewBackups)

        // Setup backup list
        val backupManager = BackupManager(requireContext())
        val backupFiles = backupManager.getBackupFiles()
        val backupItems = backupFiles.map { file ->
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(file.lastModified())
            getString(R.string.backup_from, date)
        }

        listViewBackups.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            backupItems
        )

        btnCreateBackup.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            val transactionManager = mainActivity.getTransactionManager()
            val backupPath = backupManager.createBackup(transactionManager)
            if (backupPath != null) {
                onBackupCreated?.invoke(backupPath)
                Toast.makeText(context, R.string.backup_created, Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(context, R.string.backup_failed, Toast.LENGTH_SHORT).show()
            }
        }

        btnClearBackups.setOnClickListener {
            if (backupManager.clearAllBackups()) {
                Toast.makeText(context, R.string.all_backups_cleared, Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(context, R.string.failed_to_clear_backups, Toast.LENGTH_SHORT).show()
            }
        }

        listViewBackups.setOnItemClickListener { _, _, position, _ ->
            val backupFile = backupFiles[position]
            val backupData = backupManager.restoreBackup(backupFile.absolutePath)
            if (backupData != null) {
                onBackupRestored?.invoke(backupData)
                Toast.makeText(context, R.string.backup_restored, Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(context, R.string.backup_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun newInstance(
            onBackupCreated: (String) -> Unit,
            onBackupRestored: (BackupManager.BackupData) -> Unit
        ): BackupRestoreDialog {
            return BackupRestoreDialog().apply {
                this.onBackupCreated = onBackupCreated
                this.onBackupRestored = onBackupRestored
            }
        }
    }
} 