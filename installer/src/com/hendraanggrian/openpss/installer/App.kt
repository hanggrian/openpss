package com.hendraanggrian.openpss.installer

import javafx.application.Application
import javafx.stage.Stage
import ktfx.application.launch
import ktfx.layouts.listView
import ktfx.layouts.scene

class App : Application() {

    companion object {
        @JvmStatic fun main(args: Array<String>) = launch<App>(*args)
    }

    override fun start(stage: Stage) {
        stage.run {
            scene = scene {
                listView<String> {

                }
            }
            show()
        }
    }
}