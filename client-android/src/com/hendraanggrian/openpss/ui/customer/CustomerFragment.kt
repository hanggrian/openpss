package com.hendraanggrian.openpss.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.ui.BaseFragment
import com.hendraanggrian.openpss.ui.util.autoHide
import com.hendraanggrian.recyclerview.widget.PaginatedRecyclerView
import kotlinx.android.synthetic.main.fragment_customer.*
import kotlinx.coroutines.runBlocking

class CustomerFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private companion object {
        const val COUNT = 20
    }

    private lateinit var customerAdapter: CustomerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_customer, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        customerSearch.input.run {
            hint = getString(R2.string.search_customer)
            addTextChangedListener(onTextChanged = { _, _, _, _ ->
                onRefresh()
            })
        }
        customerRefresh.run {
            setColorSchemeResources(R.color.blue)
            customerRefresh.setOnRefreshListener(this@CustomerFragment)
        }
        customerAdapter = CustomerAdapter()
        customerRecycler.run {
            adapter = customerAdapter
            pagination = object : PaginatedRecyclerView.Pagination() {
                override fun getPageStart(): Int = 0
                override fun onPaginate(page: Int) {
                    runCatching {
                        val (pageCount, customers) = runBlocking {
                            api.getCustomers(customerSearch.input.text, page, COUNT)
                        }
                        when {
                            pageCount < page -> notifyPaginationCompleted()
                            else -> {
                                notifyPageLoaded()
                                customerAdapter.addAll(customers)
                            }
                        }
                    }.onFailure {
                        notifyPageError()
                    }
                }
            }
            autoHide(customerButton)
        }
        onRefresh()
    }

    override fun onRefresh() {
        customerAdapter.clear()
        customerRecycler.pagination!!.notifyPaginationRestart()
        customerRefresh.isRefreshing = false
    }
}