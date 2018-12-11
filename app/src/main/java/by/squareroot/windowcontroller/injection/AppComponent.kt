package by.squareroot.windowcontroller.injection

import android.app.Application
import by.squareroot.windowcontroller.MyApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ActivitiesBindingModule::class,
    FragmentModule::class,
    AppModule::class])
interface AppComponent: AndroidInjector<MyApp> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun create(app: Application):Builder
        fun build(): AppComponent
    }
}