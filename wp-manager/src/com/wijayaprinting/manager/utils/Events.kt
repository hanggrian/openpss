@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.manager.utils

import javafx.event.Event
import javafx.event.EventType
import javafx.scene.Node

inline fun <T : Event> Node.addConsumedEventFilter(type: EventType<T>, noinline handler: (T) -> Unit) = addEventFilter(type) { event ->
    event.consume()
    handler(event)
}