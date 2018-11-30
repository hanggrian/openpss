package com.hendraanggrian.openpss.ui.invoice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hendraanggrian.openpss.R

class InvoiceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_invoice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }
}