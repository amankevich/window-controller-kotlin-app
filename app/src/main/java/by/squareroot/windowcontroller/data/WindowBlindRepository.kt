package by.squareroot.windowcontroller.data

import android.util.Log
import by.squareroot.windowcontroller.api.*
import by.squareroot.windowcontroller.consts.VERSION_NODE_MCU
import by.squareroot.windowcontroller.consts.VERSION_ORANGE_PI
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit


@Singleton
class WindowBlindRepository @Inject constructor(private val database: AppDatabase) {
    private val retrofitCache = HashMap<String, WindowController>()

    private fun getRetrofitCached(window: WindowBlind): WindowController {
        synchronized(retrofitCache) {
            return retrofitCache[window.address] ?: when (window.apiVersion) {
                VERSION_ORANGE_PI -> {
                    WindowControllerOrangePIAdapter(
                        Retrofit.Builder()
                            .baseUrl("http://${window.address}")
                            .client(buildOkHttpClient())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(WindowControllerOrangePI::class.java)
                    )
                }

                VERSION_NODE_MCU -> {
                    WindowControllerNodeMCUAdapter(
                        Retrofit.Builder()
                            .baseUrl("http://${window.address}")
                            .client(buildOkHttpClient())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(WindowControllerNodeMCU::class.java)
                    )
                }

                else -> throw IllegalArgumentException("api version ${window.apiVersion} not supported for $window")
            }.also {
                retrofitCache[window.address] = it
            }
        }
    }

    private fun getRetrofitObservable(window: WindowBlind): Observable<WindowController> {
        return Observable
            .just(getRetrofitCached(window))
            .onExceptionResumeNext(Observable.empty())
    }

    private fun buildOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    fun getWindowBlinds(): Observable<List<WindowBlind>> {
        return database.windowDao().getAll()
    }

    fun createNewWindow(window: WindowBlind): Single<Boolean> {
        return Single.create<Boolean>() {
            database.windowDao().insertAll(window)
            it.onSuccess(true)
        }
    }

    private fun checkConnectionWithError(window: WindowBlind): Observable<WindowBlind> {
        Log.d(TAG, "checkConnection for device ${window.address}")

        val api = getRetrofitCached(window)
        return api.position().flatMap {
            window.connected = true
            Observable.just(window)
        }
    }

    fun checkConnection(window: WindowBlind): Observable<WindowBlind> {
        return getRetrofitObservable(window)
            .flatMap {
                it.position()
                    .onErrorResumeNext(Observable.empty())
            }.flatMap {
                Log.d(TAG, "window ${window} is connected")
                window.connected = true
                Observable.just(window)
            }
    }

    fun getPosition(window: WindowBlind): Observable<WindowControllerPosition> {
        return getRetrofitObservable(window)
            .flatMap {
                it.position()
            }
    }

    fun delete(window: WindowBlind): Single<Boolean> {
        return Single.create<Boolean> {
            Log.d(TAG, "delete window controller in db ${window}")
            database.windowDao().delete(window)
            it.onSuccess(true)
        }
    }

    fun update(window: WindowBlind): Single<Boolean> {
        return Single.create<Boolean> {
            Log.d(TAG, "update window controller in db ${window}")
            database.windowDao().update(window)
            it.onSuccess(true)
        }
    }

    fun stopWindowBlind(window: WindowBlind): Completable {
        return Completable.fromObservable(
            getRetrofitObservable(window)
                .flatMap {
                    it.stop()
                }
        )
    }

    fun upWindowBlind(window: WindowBlind): Completable {
        return Completable.fromObservable(
            getRetrofitObservable(window)
                .flatMap {
                    it.autoMove(WindowControllerDirection.UP)
                }
        )
    }

    fun downWindowBlind(window: WindowBlind): Completable {
        return Completable.fromObservable(
            getRetrofitObservable(window)
                .flatMap {
                    it.autoMove(WindowControllerDirection.DOWN)
                }
        )
    }

    fun manualMove(
        window: WindowBlind,
        direction: WindowControllerDirection,
        side: WindowControllerSide,
        time: Int
    ): Completable {
        return Completable.fromObservable(
            getRetrofitObservable(window)
                .flatMap {
                    it.manualMove(direction, side, time)
                }
        )
    }

    fun getAlarms(window: WindowBlind): Observable<WindowControllerAlarms> {
        return getRetrofitObservable(window)
            .flatMap {
                it.getAlarms()
            }
    }

    fun setAlarm(
        window: WindowBlind,
        direction: WindowControllerDirection,
        time: Long
    ): Completable {
        return Completable.fromObservable(
            getRetrofitObservable(window)
                .flatMap {
                    it.setAlarm(direction, time)
                }
        )
    }

    fun getSettings(window: WindowBlind): Observable<WindowControllerSettings> {
        return getRetrofitObservable(window)
            .flatMap {
                it.getSettings()
            }
    }

    fun setSetting(
        window: WindowBlind,
        side: WindowControllerSide,
        direction: WindowControllerDirection,
        time: Int
    ): Completable {
        return Completable.fromObservable(
            getRetrofitObservable(window)
                .flatMap {
                    it.setSetting(direction, side, time)
                }
        )
    }

    companion object {
        const val TAG = "WindowRepo"
    }
}