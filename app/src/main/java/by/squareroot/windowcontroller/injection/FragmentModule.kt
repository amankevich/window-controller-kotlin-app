package by.squareroot.windowcontroller.injection

import by.squareroot.windowcontroller.ui.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeWindowBlindListFragment(): WindowBlindListFragment

    @ContributesAndroidInjector
    abstract fun contributeNewWindowBlindFragment(): NewWindowBlindFragment

    @ContributesAndroidInjector
    abstract fun contributeWindowBlindControlNodeMCUFragment(): WindowBlindControlNodeMCUFragment

    @ContributesAndroidInjector
    abstract fun contributeWindowBlindControlOrangePIFragment(): WindowBlindControlOrangePIFragment

    @ContributesAndroidInjector
    abstract fun contributeWindowControlFragment(): WindowControlFragment

    @ContributesAndroidInjector
    abstract fun contributeWindowBlindEditFragment(): WindowBlindEditFragment

    @ContributesAndroidInjector
    abstract fun contributeWindowBlindDetailsFragment(): WindowBlindDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeWindowControllerSettings(): WindowControllerSettingsFragment
}