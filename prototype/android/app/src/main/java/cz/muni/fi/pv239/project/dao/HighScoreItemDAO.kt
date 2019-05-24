package cz.muni.fi.pv239.project.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import cz.muni.fi.pv239.project.entities.HighScoreItem

@Dao
interface HighScoreItemDAO {

    @Query("SELECT * from HighScores")
    fun getAll(): List<HighScoreItem>

    @Insert(onConflict = REPLACE)
    fun insert(highScoreItem: HighScoreItem)

    @Delete
    fun delete(highScoreItem: HighScoreItem)

    @Query("DELETE from HighScores")
    fun deleteAll()
}