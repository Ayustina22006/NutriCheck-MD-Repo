package com.example.nutricheck.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nutricheck.data.entity.CapturedFoodItem

@Database(entities = [CapturedFoodItem::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun capturedFoodItemDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration strategy
        private val MIGRATION_1_TO_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("""
                    CREATE TABLE captured_food_item_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        imagePath TEXT NOT NULL,
                        mealid TEXT
                    )
                """)

                // Copy existing data (if any) from the old table to the new table
                db.execSQL("""
                    INSERT INTO captured_food_item_new (id, imagePath, mealid)
                    SELECT id, imagePath, '' FROM captured_food_item
                """)

                // Remove the old table
                db.execSQL("DROP TABLE captured_food_item")

                // Rename the new table to the original table name
                db.execSQL("ALTER TABLE captured_food_item_new RENAME TO captured_food_item")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "food_database"
                )
                    .addMigrations(MIGRATION_1_TO_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}