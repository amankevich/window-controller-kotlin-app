package by.squareroot.windowcontroller.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.squareroot.windowcontroller.api.*
import by.squareroot.windowcontroller.data.WindowBlind
import by.squareroot.windowcontroller.data.WindowBlindRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

class WindowBlindViewModel(val windowBlindRepository: WindowBlindRepository) : ViewModel() {
    val allWindowBlinds = windowBlindRepository.getWindowBlinds()

    val activeWindowBlind = MutableLiveData<WindowBlind>()

    fun checkConnection(window: WindowBlind): Observable<WindowBlind> {
        return windowBlindRepository.checkConnection(window)
    }

    fun delete(window: WindowBlind): Single<Boolean> {
        return windowBlindRepository.delete(window)
    }

    fun update(window: WindowBlind): Single<Boolean> {
        return windowBlindRepository.update(window)
    }

    fun stopWindowBlind(window: WindowBlind): Completable {
        return windowBlindRepository.stopWindowBlind(window)
    }

    fun getPosition(window: WindowBlind): Observable<WindowControllerPosition> {
        return windowBlindRepository.getPosition(window)
    }

    fun downWindowBlind(window: WindowBlind): Completable {
        return windowBlindRepository.downWindowBlind(window)
    }

    fun upWindowBlind(window: WindowBlind): Completable {
        return windowBlindRepository.upWindowBlind(window)
    }

    fun manualMove(
        window: WindowBlind,
        direction: WindowControllerDirection,
        side: WindowControllerSide,
        time: Int
    ): Completable {
        return windowBlindRepository.manualMove(window, direction, side, time)
    }

    fun getAlarms(window: WindowBlind): Observable<WindowControllerAlarms> {
        return windowBlindRepository.getAlarms(window)
    }

    fun setAlarm(
        window: WindowBlind,
        direction: WindowControllerDirection,
        time: Long
    ): Completable {
        return windowBlindRepository.setAlarm(window, direction, time)
    }

    fun getSettings(window: WindowBlind): Observable<WindowControllerSettings> {
        return windowBlindRepository.getSettings(window)
    }

    fun setSetting(
        window: WindowBlind,
        side: WindowControllerSide,
        direction: WindowControllerDirection,
        time: Int
    ): Completable {
        return windowBlindRepository.setSetting(window, side, direction, time)
    }
}