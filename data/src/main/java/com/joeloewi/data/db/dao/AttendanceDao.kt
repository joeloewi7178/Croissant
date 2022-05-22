package com.joeloewi.data.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.joeloewi.data.entity.AttendanceEntity
import com.joeloewi.data.entity.relational.AttendanceWithGamesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendanceEntity: AttendanceEntity): Long

    @Update
    suspend fun update(vararg attendanceEntities: AttendanceEntity): Int

    @Delete
    suspend fun delete(vararg attendanceEntities: AttendanceEntity): Int

    @Query("SELECT * FROM AttendanceEntity WHERE uid = :uid")
    suspend fun getOneByUid(uid: Long): AttendanceEntity

    @Transaction
    @Query("SELECT * FROM AttendanceEntity WHERE id = :id")
    suspend fun getOne(id: Long): AttendanceWithGamesEntity

    @Transaction
    @Query("SELECT * FROM AttendanceEntity WHERE id IN (:ids)")
    suspend fun getByIds(vararg ids: Long): List<AttendanceWithGamesEntity>

    @Transaction
    @Query("SELECT * FROM AttendanceEntity ORDER BY createdAt DESC")
    fun getAllPaged(): PagingSource<Int, AttendanceWithGamesEntity>

    @Transaction
    @Query("SELECT * FROM AttendanceEntity ORDER BY createdAt DESC")
    fun getAll(): Flow<List<AttendanceWithGamesEntity>>

    @Transaction
    @Query("SELECT * FROM AttendanceEntity ORDER BY createdAt DESC")
    suspend fun getAllOneShot(): List<AttendanceEntity>
}