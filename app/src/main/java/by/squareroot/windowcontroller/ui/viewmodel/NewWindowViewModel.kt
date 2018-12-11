package by.squareroot.windowcontroller.ui.viewmodel

import androidx.lifecycle.ViewModel
import by.squareroot.windowcontroller.data.WindowBlind
import by.squareroot.windowcontroller.data.WindowBlindRepository
import io.reactivex.Single

class NewWindowViewModel(private val windowBlindRepository: WindowBlindRepository) : ViewModel() {
    fun createNewWindow(windowBlind: WindowBlind): Single<Boolean> {
        return windowBlindRepository.createNewWindow(windowBlind)
    }
}