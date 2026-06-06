package com.wallet.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wallet.app.data.local.dao.*
import com.wallet.app.data.local.entity.*
import com.wallet.app.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        private class SeedDatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedCategories(database.categoryDao())
                        seedWallets(database.walletDao())
                    }
                }
            }
        }

        private suspend fun seedCategories(categoryDao: CategoryDao) {
            if (categoryDao.getCount() == 0) {
                categoryDao.insertAll(
                    Constants.DEFAULT_CATEGORIES.mapIndexed { index, cat ->
                        CategoryEntity(
                            name = cat.name,
                            emoji = cat.emoji,
                            color = cat.color,
                            sortOrder = index
                        )
                    }
                )
            }
        }

        private suspend fun seedWallets(walletDao: WalletDao) {
            walletDao.insert(
                WalletEntity(
                    name = "Cash",
                    emoji = "\uD83D\uDCB5",
                    balance = 0.0,
                    isPrimary = true
                )
            )
        }
    }
}
