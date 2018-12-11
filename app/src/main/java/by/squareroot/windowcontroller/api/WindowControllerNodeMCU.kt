package by.squareroot.windowcontroller.api

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WindowControllerNodeMCU {
    @GET("/status")
    fun getStatus(): Observable<NodeMCUStatus>

    @GET("/stop")
    fun stop(): Observable<NodeMCUResponse>

    @GET("/up")
    fun up(): Observable<NodeMCUResponse>

    @GET("/down")
    fun down(): Observable<NodeMCUResponse>

    @GET("/tasks")
    fun getScheduledAlarms(): Observable<WindowControllerAlarms>

    @GET("/schedule")
    fun scheduleAlarm(@Query("direction") direction: String, @Query("time") time: Long): Observable<NodeMCUResponse>

    @GET("/settings")
    fun getSettings(): Observable<WindowControllerSettings>

    @GET("/settings")
    fun setSetting(
        @Query("motor") side: String,
        @Query("direction") direction: String, @Query("time") time: Int
    ): Observable<NodeMCUResponse>
}