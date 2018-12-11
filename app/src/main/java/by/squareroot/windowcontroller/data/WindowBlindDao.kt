package by.squareroot.windowcontroller.data

import androidx.room.*
import io.reactivex.Observable

@Dao
interface WindowBlindDao {
    @Query("SELECT * FROM window_blinds")
    fun getAll(): Observable<List<WindowBlind>>

    @Insert
    fun insertAll(vararg windowBlind: WindowBlind)

    @Delete
    fun delete(windowBlind: WindowBlind)

    @Update
    fun update(vararg windowBlind: WindowBlind)
}