package com.wijayaprinting.manager.dialog

import com.wijayaprinting.manager.R
import com.wijayaprinting.manager.Resourceful
import javafx.scene.control.Dialog
import javafx.scene.image.Image
import javafx.scene.text.Font.font
import javafx.scene.text.FontWeight.BLACK
import javafx.scene.text.FontWeight.LIGHT
import kotfx.*
import java.lang.System.lineSeparator
import java.util.*

class AboutDialog(override val resources: ResourceBundle) : Dialog<Unit>(), Resourceful {

    init {
        title = getString(R.string.about)
        content = hbox {
            imageView(Image(R.png.logo_launcher)) {
                fitWidth = 156.0
                fitHeight = 156.0
            }
            vbox {
                textFlow {
                    text("Wijaya Printing ") { font = font(null, BLACK, 24.0) }
                    text("Manager") { font = font(null, LIGHT, 24.0) }
                    text(lineSeparator())
                }
            } marginLeft 32.0
        }

        /*expandableContent = textFlow {
            text(getString(R.string.about_info))
            hyperlink("https://github.com/WijayaPrinting/") { setOnAction { getDesktop().browse(URI(text)) } }
            text(getProperty("line.separator"))
            text("Manager ${com.wijayaprinting.manager.BuildConfig.VERSION}")
            text(getProperty("line.separator"))
            text("Data ${com.wijayaprinting.data.BuildConfig.VERSION}")
        }*/
    }
}