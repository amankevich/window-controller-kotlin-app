package by.squareroot.windowcontroller.injection

import by.squareroot.windowcontroller.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesBindingModule {
    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity
}