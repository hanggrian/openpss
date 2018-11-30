@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

inline fun AppCompatActivity.replaceFragment(@IdRes containerViewId: Int, fragment: Fragment) = supportFragmentManager
    .beginTransaction()
    .replace(containerViewId, fragment)
    .commitNow()