package com.wallet.app.util

import android.content.Context
import android.net.Uri
import java.io.BufferedWriter
import java.io.OutputStreamWriter

object CsvExporter {

    data class ExportRow(
        val date: String,
        val type: String,
        val category: String,
        val amount: String,
        val note: String,
        val wallet: String
    )

    fun exportToCsv(
        context: Context,
        uri: Uri,
        rows: List<ExportRow>
    ): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                    writer.write("Date,Type,Category,Amount,Note,Wallet")
                    writer.newLine()
                    rows.forEach { row ->
                        writer.write(
                            "${escapeCsv(row.date)}," +
                            "${escapeCsv(row.type)}," +
                            "${escapeCsv(row.category)}," +
                            "${escapeCsv(row.amount)}," +
                            "${escapeCsv(row.note)}," +
                            "${escapeCsv(row.wallet)}"
                        )
                        writer.newLine()
                    }
                }
            } ?: false
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }
}
