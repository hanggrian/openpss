package com.hendraanggrian.openpss.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hendraanggrian.openpss.R

/** Temporary fragment that will be phased out once actual fragment is implemented. */
class UnsupportedFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_unsupported, parent, false)
}