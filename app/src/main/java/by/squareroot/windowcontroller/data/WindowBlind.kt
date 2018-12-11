package by.squareroot.windowcontroller.data

import androidx.room.*

@Entity(tableName = "window_blinds")
@TypeConverters(AddressConverters::class)
data class WindowBlind(
    @PrimaryKey(autoGenerate = true) var id: Int,

    var name: String,

    var address: String,

    @ColumnInfo(name = "api_version") var apiVersion: Int,

    @Ignore
    var connected: Boolean = false
) {
    constructor() : this(0, "", "", 0, false)
}