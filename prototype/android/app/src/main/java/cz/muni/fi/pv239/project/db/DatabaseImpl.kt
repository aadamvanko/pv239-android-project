package cz.muni.fi.pv239.project.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import cz.muni.fi.pv239.project.dao.HighScoreItemDAO
import cz.muni.fi.pv239.project.dao.UserDAO
import cz.muni.fi.pv239.project.entities.HighScoreItem
import cz.muni.fi.pv239.project.entities.User

@Database(entities = arrayOf(User::class, HighScoreItem::class), version = 2)
abstract class DatabaseImpl : RoomDatabase() {

    abstract fun userDAO(): UserDAO
    abstract fun highScoreItemDAO(): HighScoreItemDAO

    companion object {
        private var INSTANCE: DatabaseImpl? = null

        fun getInstance(context: Context): DatabaseImpl? {
            if (INSTANCE == null) {
                synchronized(DatabaseImpl::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            DatabaseImpl::class.java, "color_jump.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
