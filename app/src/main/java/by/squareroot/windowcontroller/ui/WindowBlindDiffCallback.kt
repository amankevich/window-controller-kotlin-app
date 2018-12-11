package by.squareroot.windowcontroller.ui

import androidx.recyclerview.widget.DiffUtil
import by.squareroot.windowcontroller.data.WindowBlind

class WindowBlindDiffCallback : DiffUtil.ItemCallback<WindowBlind>() {
    override fun areContentsTheSame(oldItem: WindowBlind, newItem: WindowBlind): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: WindowBlind, newItem: WindowBlind): Boolean {
        return oldItem.id == newItem.id
    }
}