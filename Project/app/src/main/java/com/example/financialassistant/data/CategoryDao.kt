package com.example.financialassistant.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories ORDER BY CASE WHEN LOWER(name) = 'other' THEN 1 ELSE 0 END ASC, isDefault DESC, name ASC")
    fun getAllCategories(): Flow<List<Category>>
}
