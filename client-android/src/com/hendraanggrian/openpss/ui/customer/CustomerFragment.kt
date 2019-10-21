package com.hendraanggrian.openpss.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.ui.BaseFragment
import com.hendraanggrian.openpss.ui.util.autoHide
import com.hendraanggrian.recyclerview.widget.PaginatedRecyclerView
import kotlinx.android.synthetic.main.fragment_customer.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class CustomerFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private companion object {
        const val COUNT = 20
    }

    private lateinit var customerAdapter: CustomerAdapter

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_customer, parent, false)

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
                    runBlocking {
                        runCatching {
                            val (pageCount, customers) = withContext(Dispatchers.IO) {
                                OpenPSSApi.getCustomers(customerSearch.input.text, page, COUNT)
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
            }
            autoHide(customerButton)
        }
    }

    override fun onRefresh() {
        customerAdapter.clear()
        customerRecycler.pagination!!.notifyPaginationRestart()
        customerRefresh.isRefreshing = false
    }
}
