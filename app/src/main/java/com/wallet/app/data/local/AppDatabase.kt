package com.wallet.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wallet.app.data.local.dao.*
import com.wallet.app.data.local.entity.*
import com.wallet.app.util.Constants

@Database(
    entities = [
        WalletEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        RecurringEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun recurringDao(): RecurringDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                if (INSTANCE != null) return INSTANCE!!
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .addCallback(SeedDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Seed data using raw SQL in the Room callback.
         * This runs synchronously during DB creation, before any DAO access.
         * Uses SupportSQLiteDatabase directly since the Room instance isn't available yet.
         */
        private class SeedDatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                try {
                    // Insert default categories
                    Constants.DEFAULT_CATEGORIES.forEachIndexed { index, cat ->
                        db.execSQL(
                            "INSERT INTO categories (name, emoji, color, sortOrder) VALUES (?, ?, ?, ?)",
                            arrayOf<Any>(cat.name, cat.emoji, cat.color, index)
                        )
                    }
                    // Insert default wallet
                    val now = System.currentTimeMillis()
                    db.execSQL(
                        "INSERT INTO wallets (name, emoji, balance, isPrimary, createdAt) VALUES (?, ?, ?, ?, ?)",
                        arrayOf<Any>("Cash", "\uD83D\uDCB5", 0.0, 1, now)
                    )
                } catch (e: Exception) {
                    android.util.Log.e("WalletApp", "Failed to seed database", e)
                }
            }
        }
    }
}
