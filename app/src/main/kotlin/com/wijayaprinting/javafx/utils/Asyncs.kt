@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.javafx.utils

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler.platform
import io.reactivex.schedulers.Schedulers.io

inline fun <T> Observable<T>.multithread(): Observable<T> = subscribeOn(io()).observeOn(platform())

inline fun <T> Single<T>.multithread(): Single<T> = subscribeOn(io()).observeOn(platform())

inline fun Completable.multithread(): Completable = subscribeOn(io()).observeOn(platform())