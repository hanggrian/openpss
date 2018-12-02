@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.popup

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

inline fun DialogFragment.show(manager: FragmentManager) = show(manager, null)