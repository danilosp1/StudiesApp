package com.example.studies.data.dao

import androidx.room.*
import com.example.studies.data.model.MaterialLinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialLinkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterialLink(materialLink: MaterialLinkEntity)

    @Update
    suspend fun updateMaterialLink(materialLink: MaterialLinkEntity)

    @Delete
    suspend fun deleteMaterialLink(materialLink: MaterialLinkEntity)

    @Query("SELECT * FROM material_links WHERE disciplineId = :disciplineId ORDER BY id ASC")
    fun getMaterialLinksByDiscipline(disciplineId: Long): Flow<List<MaterialLinkEntity>>

    @Query("SELECT * FROM material_links WHERE id = :linkId")
    fun getMaterialLinkById(linkId: Long): Flow<MaterialLinkEntity?>
}