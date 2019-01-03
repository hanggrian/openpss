package com.hendraanggrian.openpss.ui.customer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.data.Customer
import com.hendraanggrian.openpss.ui.BaseAdapter

class CustomerAdapter : BaseAdapter<Customer, CustomerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_customer,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val customer = get(position)
        holder.typeImage.setImageResource(
            when {
                customer.isCompany -> R.drawable.ic_company
                else -> R.drawable.ic_person
            }
        )
        holder.nameText.text = customer.name
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeImage: ImageView = itemView.findViewById(R.id.typeImage)
        val nameText: TextView = itemView.findViewById(R.id.nameText)
    }
}