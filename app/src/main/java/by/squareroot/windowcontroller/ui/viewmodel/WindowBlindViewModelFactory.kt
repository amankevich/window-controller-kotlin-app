package by.squareroot.windowcontroller.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.squareroot.windowcontroller.data.WindowBlindRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WindowBlindViewModelFactory @Inject constructor(private val repository: WindowBlindRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WindowBlindViewModel(repository) as T
    }
}