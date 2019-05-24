package cz.muni.fi.pv239.project.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "HighScores")
data class HighScoreItem(@PrimaryKey(autoGenerate = true) var id: Long?,
                         @ColumnInfo(name = "username") var username: String,
                         @ColumnInfo(name = "score") var score: Long) {

    constructor() : this(null, "", 0L)

}