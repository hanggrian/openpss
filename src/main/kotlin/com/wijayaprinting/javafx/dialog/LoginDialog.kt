package com.wijayaprinting.javafx.dialog

import com.wijayaprinting.javafx.Language
import com.wijayaprinting.javafx.R
import com.wijayaprinting.javafx.Resourced
import com.wijayaprinting.javafx.control.*
import com.wijayaprinting.javafx.io.LoginFile
import com.wijayaprinting.javafx.io.PreferencesFile
import com.wijayaprinting.mysql.MySQL
import com.wijayaprinting.mysql.dao.Staff
import com.wijayaprinting.mysql.dao.Staffs
import javafx.event.ActionEvent
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import kotfx.bindings.not
import kotfx.bindings.or
import kotfx.bindings.stringBindingOf
import kotfx.dialogs.errorAlert
import kotfx.dialogs.infoAlert
import kotfx.exitFXApplication
import kotfx.runLater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
internal class LoginDialog(override val resources: ResourceBundle, requiredStaffLevel: Int) : Dialog<Any>(), Resourced {

    val mysqlFile = LoginFile()

    val content = Content()
    val expandableContent = ExpandableContent()
    val tryButton = ButtonType(getString(R.strings.try_connect), ButtonBar.ButtonData.CANCEL_CLOSE)
    val loginButton = ButtonType(getString(R.strings.login), ButtonBar.ButtonData.OK_DONE)

    init {
        val languageComboBox = ComboBox<Language>(Language.listAll())
        languageComboBox.selectionModel.select(Language.parse(resources.locale.language))
        languageComboBox.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            PreferencesFile().apply { language.set(newValue.locale) }.save()
            close()
            infoAlert(getString(R.strings.notice_restart)).showAndWait()
            exitFXApplication()
        }

        title = getString(R.strings.wp_login)
        headerText = getString(R.strings.wp_login)
        graphic = languageComboBox

        dialogPane.content = content
        dialogPane.expandableContent = expandableContent
        dialogPane.buttonTypes.addAll(tryButton, loginButton)

        dialogPane.lookupButton(tryButton).addEventFilter(ActionEvent.ACTION) { event ->
            mysqlFile.save()
            event.consume()
            try {
                expandableContent.connect()
                transaction { }
                infoAlert(getString(R.strings.notice_success_connection)).show()
            } catch (e: Exception) {
                errorAlert(e.message!!).show()
            }
        }
        dialogPane.lookupButton(loginButton).addEventFilter(ActionEvent.ACTION) { event ->
            mysqlFile.save()
            event.consume()
            expandableContent.connect()
            var staff: Staff? = null
            val name = content.staffField.text
            val id = name.toIntOrNull()
            transaction {
                val correctPassword = Staffs.password eq content.passwordField.text
                if (id != null) staff = Staff.find { Staffs.id eq id and correctPassword }.firstOrNull()
                if (staff == null) staff = Staff.find { Staffs.name eq name and correctPassword }.firstOrNull()
            }
            when {
                staff == null -> errorAlert(getString(R.strings.notice_failure_credentials)).showAndWait()
                staff!!.level < requiredStaffLevel -> errorAlert(getString(R.strings.notice_failure_level)).showAndWait()
                else -> {
                    result = staff!!
                    close()
                }
            }
        }
        dialogPane.lookupButton(tryButton).disableProperty().bind(expandableContent.validBinding)
        dialogPane.lookupButton(loginButton).disableProperty().bind(content.validBinding or expandableContent.validBinding)

        content.staffField.textProperty().bindBidirectional(mysqlFile.staff)
        expandableContent.ipField.textProperty().bindBidirectional(mysqlFile.ip)
        expandableContent.portField.textProperty().bindBidirectional(mysqlFile.port)
        expandableContent.userField.textProperty().bindBidirectional(mysqlFile.user)
        expandableContent.passwordField.textProperty().bindBidirectional(mysqlFile.password)

        runLater {
            when {
                content.staffField.text.isEmpty() -> content.staffField.requestFocus()
                else -> content.passwordField.requestFocus()
            }
        }
    }

    inner class Content : GridPane() {
        val staffLabel = Label(getString(R.strings.staff))
        val staffField = TextField2(getString(R.strings.idorname))
        val passwordLabel = Label(getString(R.strings.password))
        val passwordField = PasswordField2(getString(R.strings.password)).apply { tooltip = Tooltip() }
        val passwordToggle = ImageToggleButton(R.png.ic_visibility_18dp, R.png.ic_visibility_off_18dp)

        val validBinding = staffField.textProperty().isEmpty or passwordField.textProperty().isEmpty

        init {
            hgap = 8.0
            vgap = 8.0

            add(staffLabel, 0, 0)
            add(staffField, 1, 0, 2, 1)
            add(passwordLabel, 0, 1)
            add(passwordField, 1, 1)
            add(passwordToggle, 2, 1)

            passwordField.tooltip.textProperty().bind(stringBindingOf(passwordField.textProperty(), passwordToggle.selectedProperty()) {
                when {
                    !passwordToggle.isSelected -> getString(R.strings.password_see)
                    else -> passwordField.text
                }
            })
        }
    }

    inner class ExpandableContent : GridPane() {
        val title = Label("MySQL").apply {
            styleClass.add("header-panel")
            isWrapText = true
            alignment = Pos.CENTER_LEFT
            maxWidth = Double.MAX_VALUE
            maxHeight = Double.MAX_VALUE
        }
        val ipLabel = Label("IP Address")
        val ipField = IPField("127.0.0.1")
        val portLabel = Label("Port")
        val portField = IntField("3306")
        val userLabel = Label("User")
        val userField = TextField2("Non-root user")
        val passwordLabel = Label("Password")
        val passwordField = PasswordField2("Password").apply { tooltip = Tooltip() }
        val passwordToggle = ImageToggleButton(R.png.ic_visibility_18dp, R.png.ic_visibility_off_18dp)

        val validBinding = not(ipField.validProperty) or
                portField.textProperty().isEmpty or
                userField.textProperty().isEmpty or
                passwordField.textProperty().isEmpty

        init {
            hgap = 8.0
            vgap = 8.0

            add(title, 0, 0, 3, 1)
            add(ipLabel, 0, 1)
            add(ipField, 1, 1, 2, 1)
            add(portLabel, 0, 2)
            add(portField, 1, 2, 2, 1)
            add(userLabel, 0, 3)
            add(userField, 1, 3, 2, 1)
            add(passwordLabel, 0, 4)
            add(passwordField, 1, 4)
            add(passwordToggle, 2, 4)

            passwordField.tooltip.textProperty().bind(stringBindingOf(passwordField.textProperty(), passwordToggle.selectedProperty()) {
                when {
                    !passwordToggle.isSelected -> getString(R.strings.notice_failure_credentials)
                    else -> passwordField.text
                }
            })
        }

        fun connect() = MySQL.connect(
                ipField.text,
                portField.text,
                userField.text,
                passwordField.text)
    }
}