package com.hendraanggrian.openpss.ui

import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class BaseAdapter<E, VH : RecyclerView.ViewHolder>(
    private val list: MutableList<E> = mutableListOf()
) : RecyclerView.Adapter<VH>(), MutableList<E> by list {

    override fun getItemCount(): Int = size

    override fun add(element: E): Boolean {
        val success = list.add(element)
        if (success) {
            GlobalScope.launch(Dispatchers.Main.immediate) {
                notifyItemInserted(lastIndex)
            }
        }
        return success
    }

    override fun addAll(elements: Collection<E>): Boolean {
        val start = size + 1
        val success = list.addAll(elements)
        GlobalScope.launch(Dispatchers.Main.immediate) {
            notifyItemRangeInserted(start, elements.size)
        }
        return success
    }

    override fun clear() {
        val size = list.size
        list.clear()
        GlobalScope.launch(Dispatchers.Main.immediate) {
            notifyItemRangeRemoved(0, size)
        }
    }
}