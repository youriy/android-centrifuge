package com.app.monitor.db.log

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LogDao {

    @Query("SELECT * FROM log WHERE `code` = :code AND `end` is not null ORDER BY id DESC")
    fun getAllByCode(code: String): List<Log>

    @Query("SELECT COUNT(*) FROM log WHERE `view` = 0")
    fun getAllCount(): LiveData<String>

    @Query("SELECT * FROM log WHERE `code` = :code AND `status` = :status AND `end` is null ORDER BY id DESC LIMIT 1")
    fun getWarningOrError(code: String, status: String): Log?

    @Query("DELETE FROM log WHERE code=:code")
    fun deleteLogsByCode(code: String)

    @Query("UPDATE log SET `view`=1 WHERE id = :id")
    fun updateLogViewById(id: Int)

    @Query("DELETE FROM log")
    fun deleteAllLog()

    @Query("DELETE FROM log WHERE id = :id")
    fun deleteLogById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg log: Log)

    @Update
    fun update(log: Log)

    @Delete
    fun delete(log: Log)
}