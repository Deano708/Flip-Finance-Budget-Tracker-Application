package com.example.flipfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flipfinance.data.local.Entities.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    // Fetches static System defaults and custom user-created categories
    @Query("SELECT * FROM categories WHERE userId = :userId OR isCustom = 0 ORDER BY name ASC")
    fun getCategoriesByUser(userId: String): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Query("DELETE FROM categories WHERE categoryId = :id AND userId = :userId")
    suspend fun deleteCategory(id: String, userId: String)
}