package com.example.financialassistant.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name
    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)
}

@Database(entities = [Transaction::class, Category::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "financial_architect_db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed default categories
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val defaults = listOf(
                                    Category(name = "Food", iconName = "Restaurant", colorHex = "#E53935", isDefault = true),
                                    Category(name = "Transport", iconName = "DirectionsCar", colorHex = "#1E88E5", isDefault = true),
                                    Category(name = "Shopping", iconName = "ShoppingBag", colorHex = "#8E24AA", isDefault = true),
                                    Category(name = "Health", iconName = "Favorite", colorHex = "#E91E63", isDefault = true),
                                    Category(name = "Entertainment", iconName = "Movie", colorHex = "#F4511E", isDefault = true),
                                    Category(name = "Education", iconName = "School", colorHex = "#039BE5", isDefault = true),
                                    Category(name = "Utilities", iconName = "Bolt", colorHex = "#F9A825", isDefault = true),
                                    Category(name = "Salary", iconName = "AccountBalance", colorHex = "#43A047", isDefault = true),
                                    Category(name = "Other", iconName = "MoreHoriz", colorHex = "#757575", isDefault = true)
                                )
                                defaults.forEach { database.categoryDao().insertCategory(it) }
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
