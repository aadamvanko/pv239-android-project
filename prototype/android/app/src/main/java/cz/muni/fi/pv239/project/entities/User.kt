package cz.muni.fi.pv239.project.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Users")
data class User(@PrimaryKey(autoGenerate = true) var id: Long?,
                @ColumnInfo(name = "name") var name: String,
                @ColumnInfo(name = "selected") var selected: Boolean) {

    constructor() : this(null, "", false)

}