package by.squareroot.windowcontroller.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface WindowControllerOrangePI {
    @GET("/window/status")
    fun getStatus(): Observable<OrangePIStatus>

    @GET("/window/stop")
    fun stop(): Observable<String>

    @GET("/window?direction=up")
    fun up(): Observable<String>

    @GET("/window?direction=down")
    fun down(): Observable<String>

    @GET("/controller")
    fun move(@Query("servo") side: String, @Query("direction") direction: String,
             @Query("time") time: Int): Observable<String>
}