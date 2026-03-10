package com.ae.emailrotator.data.repository

import com.ae.emailrotator.data.local.dao.ToolDao
import com.ae.emailrotator.data.mapper.toDomain
import com.ae.emailrotator.data.mapper.toEntity
import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.repository.ToolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ToolRepositoryImpl @Inject constructor(
    private val dao: ToolDao
) : ToolRepository {

    override fun getAllTools(): Flow<List<Tool>> =
        dao.getAllTools().map { list -> list.map { it.toDomain() } }

    override suspend fun getToolById(id: Long): Tool? =
        dao.getById(id)?.toDomain()

    override suspend fun insertTool(tool: Tool): Long =
        dao.insert(tool.toEntity())

    override suspend fun updateTool(tool: Tool) =
        dao.update(tool.toEntity())

    override suspend fun deleteTool(id: Long) =
        dao.delete(id)

    override suspend fun getToolByName(name: String): Tool? =
        dao.getByName(name)?.toDomain()
}
