package com.wallet.app.data.repository

import androidx.compose.ui.graphics.Color
import com.wallet.app.data.local.dao.CategoryDao
import com.wallet.app.data.local.entity.CategoryEntity
import com.wallet.app.domain.model.Category
import com.wallet.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    private fun CategoryEntity.toDomain() = Category(
        id = id,
        name = name,
        emoji = emoji,
        color = Color(color),
        sortOrder = sortOrder
    )

    private fun Category.toEntity() = CategoryEntity(
        id = id,
        name = name,
        emoji = emoji,
        color = color.value.toLong(),
        sortOrder = sortOrder
    )

    override fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAllCategories().map { list -> list.map { it.toDomain() } }

    override suspend fun getCategoryById(id: Long): Category? =
        categoryDao.getCategoryById(id)?.toDomain()

    override suspend fun saveCategory(category: Category): Long =
        categoryDao.insert(category.toEntity())

    override suspend fun updateCategory(category: Category) =
        categoryDao.update(category.toEntity())

    override suspend fun deleteCategory(category: Category) =
        categoryDao.delete(category.toEntity())
}
