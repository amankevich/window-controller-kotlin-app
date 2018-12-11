package by.squareroot.windowcontroller.api

import io.reactivex.Observable

interface WindowController {
    fun stop(): Observable<Boolean>

    fun position(): Observable<WindowControllerPosition>

    fun autoMove(direction: WindowControllerDirection): Observable<Boolean>

    fun manualMove(direction: WindowControllerDirection, side: WindowControllerSide, time: Int): Observable<Boolean>

    fun getAlarms(): Observable<WindowControllerAlarms>

    fun setAlarm(direction: WindowControllerDirection, time: Long): Observable<Boolean>

    fun getSettings(): Observable<WindowControllerSettings>

    fun setSetting(direction: WindowControllerDirection, side: WindowControllerSide, time: Int): Observable<Boolean>
}

