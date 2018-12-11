package by.squareroot.windowcontroller.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import by.squareroot.windowcontroller.R
import by.squareroot.windowcontroller.consts.VERSION_NODE_MCU
import by.squareroot.windowcontroller.consts.VERSION_ORANGE_PI
import by.squareroot.windowcontroller.data.WindowBlind
import by.squareroot.windowcontroller.ui.viewmodel.WindowBlindViewModel
import by.squareroot.windowcontroller.ui.viewmodel.WindowBlindViewModelFactory
import dagger.android.support.AndroidSupportInjection
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_windowblind_list.*
import javax.inject.Inject


class WindowBlindListFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: WindowBlindViewModelFactory
    private val disposables = CompositeDisposable()
    private val checkWindowControllerConnectionSubject = PublishSubject.create<List<WindowBlind>>()
    private lateinit var myAdapter: WindowBlindAdapter;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.action_refresh -> {
                refresh()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refresh() {
        myAdapter.itemsList?.let {
            checkWindowControllerConnectionSubject.onNext(it)
        }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_windowblind_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(WindowBlindViewModel::class.java)
        val navController = Navigation.findNavController(view)

        myAdapter = WindowBlindAdapter()
        with(list) {
            layoutManager = LinearLayoutManager(context)
            adapter = myAdapter
        }

        disposables.add(myAdapter.clickSubject
            .subscribe {
                viewModel.activeWindowBlind.value = it
                when (it.apiVersion) {
                    VERSION_ORANGE_PI -> navController.navigate(R.id.action_to_orange_pi)
                    VERSION_NODE_MCU -> navController.navigate(R.id.action_to_node_mcu)
                }
            })

        disposables.add(checkWindowControllerConnectionSubject
            .flatMapIterable {
                it
            }
            .observeOn(Schedulers.io())
            .flatMap {
                viewModel.checkConnection(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                myAdapter.notifyDataSetChanged()
            })

        disposables.add(viewModel.allWindowBlinds
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isNotEmpty()) {
                    empty_list.visibility = View.GONE
                    myAdapter.submitList(it)
                } else {
                    empty_list.visibility = View.VISIBLE
                }
                checkWindowControllerConnectionSubject.onNext(it)
            })

        fab.setOnClickListener {
            navController.navigate(R.id.action_create_new)
        }
    }
}
