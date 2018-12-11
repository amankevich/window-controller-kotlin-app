package by.squareroot.windowcontroller.ui


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import by.squareroot.windowcontroller.R
import by.squareroot.windowcontroller.data.WindowBlind
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.window_row.view.*

class WindowBlindAdapter : ListAdapter<WindowBlind, WindowBlindAdapter.ViewHolder>(WindowBlindDiffCallback()) {
    private val mOnClickListener: View.OnClickListener
    val clickSubject = PublishSubject.create<WindowBlind>()
    var itemsList: List<WindowBlind>? = null
        private set

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as WindowBlind
            clickSubject.onNext(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.window_row, parent, false)
        return ViewHolder(view)
    }

    override fun submitList(list: List<WindowBlind>?) {
        super.submitList(list)
        itemsList = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.name.text = item.name
        holder.address.text = item.address
        holder.connected.setText(
            if (item.connected)
                R.string.window_row_item_connected
            else
                R.string.window_row_item_not_connected
        )

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val name: TextView = mView.window_row_name
        val address: TextView = mView.window_row_address
        val connected: TextView = mView.window_row_connected
    }
}
