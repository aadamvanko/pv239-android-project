package cz.muni.fi.pv239.project.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import cz.muni.fi.pv239.project.entities.User

@Dao
interface UserDAO {

    @Query("SELECT * from Users")
    fun getAll(): List<User>

    @Insert(onConflict = REPLACE)
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)

    @Query("DELETE from Users")
    fun deleteAll()
}