package com.app.monitor.db.monitor

import androidx.lifecycle.LiveData
import androidx.room.*
import com.app.monitor.models.DefaultMonitor
import com.app.monitor.models.ViewMonitor

@Dao
interface MonitorDao {

    @Query("SELECT *, (SELECT COUNT(id) FROM log AS l WHERE l.code=m.code AND l.`view` = 0 AND l.`end` is not null) AS count FROM monitor AS m ORDER BY `row` ASC, `code` ASC")
    fun getAll(): List<ViewMonitor>

    @Query("SELECT * FROM monitor WHERE code=:code")
    fun getByCode(code: String): Monitor?

    @Query("SELECT *, (SELECT COUNT(id) as count FROM log AS l WHERE l.code=m.code AND l.`view` = 0 AND l.`end` is not null) AS count FROM monitor AS m WHERE `status` = :status AND `monitoring` = 1 ORDER BY `row` ASC, `code` ASC")
    fun getAllByStatus(status: String): LiveData<List<ViewMonitor>>

    @Query("SELECT `code`, `index`, `title`, `status`, `row`, `update` FROM monitor WHERE `code` = :code LIMIT 1")
    fun getMonitorByCode(code: String): LiveData<DefaultMonitor>

    @Query("SELECT status FROM monitor WHERE `code` = :code LIMIT 1")
    fun getStatusByCode(code: String): String?

    @Query("UPDATE monitor SET monitoring=:monitoring WHERE code=:code")
    fun updateMonitoring(monitoring: Int, code: String)

    @Query("UPDATE monitor SET monitoring=:monitoring WHERE `row`=:row")
    fun updateMonitoringRow(monitoring: Int, row: Int)

    @Query("UPDATE monitor SET monitoring=:monitoring WHERE code!=:code")
    fun updateMonitoringAnException(monitoring: Int, code: String)

    @Query("UPDATE monitor SET monitoring=:monitoring WHERE `row`!=:row")
    fun updateMonitoringRowAnException(monitoring: Int, row: Int)

    @Query("DELETE FROM monitor")
    fun resetMonitor()

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Monitor::class)
    fun insert(vararg defaultMonitor: DefaultMonitor)

    @Update
    fun update(monitor: Monitor)

    @Delete
    fun delete(monitor: Monitor)
}