package com.hendraanggrian.openpss.ui

import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<E, VH : RecyclerView.ViewHolder>(
    private val list: MutableList<E> = mutableListOf()
) : RecyclerView.Adapter<VH>(), MutableList<E> by list {

    override fun getItemCount(): Int = size

    override fun add(element: E): Boolean {
        val success = list.add(element)
        if (success) {
            notifyItemInserted(lastIndex)
        }
        return success
    }

    override fun addAll(elements: Collection<E>): Boolean {
        val start = size + 1
        val success = list.addAll(elements)
        notifyItemRangeInserted(start, elements.size)
        return success
    }

    override fun clear() {
        val size = list.size
        list.clear()
        notifyItemRangeRemoved(0, size)
    }
}