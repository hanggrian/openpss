package com.wijayaprinting.manager.internal

import io.reactivex.disposables.Disposable

interface Registrable {

    val disposables: MutableSet<Disposable>

    fun Disposable.register() {
        disposables.add(this)
    }

    fun disposeAll() {
        if (disposables.isEmpty()) return
        disposables.forEach { it.dispose() }
        disposables.clear()
    }
}