package by.squareroot.windowcontroller

import by.squareroot.windowcontroller.injection.AppComponent
import by.squareroot.windowcontroller.injection.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class MyApp : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component : AppComponent = DaggerAppComponent
            .builder()
            .create(this)
            .build()
        return component
    }
}