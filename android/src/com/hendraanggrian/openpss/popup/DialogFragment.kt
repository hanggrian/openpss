@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.popup

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

inline fun DialogFragment.show(manager: FragmentManager) = show(manager, null)

inline fun DialogFragment.args(bundle: Bundle): DialogFragment = apply { arguments = bundle }