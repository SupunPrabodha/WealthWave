package com.example.mad3

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mad3.databinding.ActivityBackupBinding
import com.example.mad3.utils.BackupManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class BackupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBackupBinding
    private lateinit var backupAdapter: BackupAdapter
    private lateinit var transactionDatabase: TransactionDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transactionDatabase = TransactionDatabase(this)

        setupRecyclerView()
        setupButtons()
        loadBackups()
    }

    private fun setupRecyclerView() {
        backupAdapter = BackupAdapter(
            onRestoreClick = { backupFile ->
                showRestoreConfirmation(backupFile)
            },
            onDeleteClick = { backupFile ->
                showDeleteConfirmation(backupFile)
            }
        )

        binding.recyclerview.apply {
            adapter = backupAdapter
            layoutManager = LinearLayoutManager(this@BackupActivity)
        }
    }

    private fun setupButtons() {
        binding.createBackupButton.setOnClickListener {
            createBackup()
        }
    }

    private fun createBackup() {
        if (BackupManager.exportData(this, transactionDatabase)) {
            Snackbar.make(binding.root, "Backup created successfully", Snackbar.LENGTH_SHORT).show()
            loadBackups()
        } else {
            Snackbar.make(binding.root, "Failed to create backup", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun loadBackups() {
        val backups = BackupManager.getBackupFiles(this)
        backupAdapter.submitList(backups)
        binding.emptyView.visibility = if (backups.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showRestoreConfirmation(backupFile: File) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Restore Backup")
            .setMessage("Are you sure you want to restore this backup? All current transactions will be replaced.")
            .setPositiveButton("Restore") { _, _ ->
                restoreBackup(backupFile)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmation(backupFile: File) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Backup")
            .setMessage("Are you sure you want to delete this backup?")
            .setPositiveButton("Delete") { _, _ ->
                deleteBackup(backupFile)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun restoreBackup(backupFile: File) {
        if (BackupManager.importData(this, transactionDatabase, backupFile)) {
            Snackbar.make(binding.root, "Backup restored successfully", Snackbar.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Snackbar.make(binding.root, "Failed to restore backup", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun deleteBackup(backupFile: File) {
        if (BackupManager.deleteBackup(backupFile)) {
            Snackbar.make(binding.root, "Backup deleted successfully", Snackbar.LENGTH_SHORT).show()
            loadBackups()
        } else {
            Snackbar.make(binding.root, "Failed to delete backup", Snackbar.LENGTH_SHORT).show()
        }
    }
} 