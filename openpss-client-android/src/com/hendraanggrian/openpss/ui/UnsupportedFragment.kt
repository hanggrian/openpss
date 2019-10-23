package com.hendraanggrian.openpss.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.hendraanggrian.material.errorbar.indefiniteErrorbar

/** Temporary fragment that will be phased out once actual fragment is implemented. */
class UnsupportedFragment : BaseFragment() {

    private lateinit var errorbarTarget: View

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View =
        FrameLayout(context!!).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            errorbarTarget = View(context)
            addView(errorbarTarget)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        errorbarTarget.indefiniteErrorbar("Unsupported.")
    }
}
