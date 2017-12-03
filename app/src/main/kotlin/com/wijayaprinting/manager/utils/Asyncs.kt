@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.manager.utils

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler.platform
import io.reactivex.schedulers.Schedulers.io

@JvmOverloads
inline fun <T> Observable<T>.multithread(
        subscribeOn: Scheduler = io(),
        observeOn: Scheduler = platform()
): Observable<T> = subscribeOn(subscribeOn).observeOn(observeOn)

@JvmOverloads
inline fun <T> Single<T>.multithread(
        subscribeOn: Scheduler = io(),
        observeOn: Scheduler = platform()
): Single<T> = subscribeOn(subscribeOn).observeOn(observeOn)

@JvmOverloads
inline fun Completable.multithread(
        subscribeOn: Scheduler = io(),
        observeOn: Scheduler = platform()
): Completable = subscribeOn(subscribeOn).observeOn(observeOn)