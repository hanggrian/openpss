package com.hendraanggrian.openpss.ui.order

/*
class PlateReceiptDialog(val resourced: Resourced) : Dialog<Receipt>(), Resourced by resourced {

    private lateinit var tableView: TableView<Pair<Plate, IntegerProperty>>

    private var customerProperty: ObjectProperty<Customer> = SimpleObjectProperty()

    init {
        title = getString(R.string.add_plate_receipt)
        headerText = getString(R.string.add_plate_receipt)
        graphic = ImageView(R.png.ic_document)
        content = gridPane {
            gap(8)
            label(getString(R.string.date)) col 0 row 0
            label(now().toString(PATTERN_DATE)) { font = loadloadFont(latoBold, 13.0) } col 1 row 0
            label(getString(R.string.employee)) col 0 row 1
            label(EMPLOYEE) { font = loadloadFont(latoBold, 13.0) } col 1 row 1
            label(getString(R.string.customer)) col 0 row 2
            button {
                textProperty() bind stringBindingOf(customerProperty) { customerProperty.value?.toString() ?: getString(R.string.search_customer) }
                setOnAction { SearchCustomerDialog(resourced).showAndWait().ifPresent { customerProperty.set(it) } }
            } col 1 row 2
            label(getString(R.string.note)) col 0 row 3
            textArea { prefHeight = 120.0 } col 1 row 3
            label(getString(R.string.orders)) col 0 row 4
            tableView = tableView<Pair<Plate, IntegerProperty>> {
                prefHeight = 240.0
                isEditable = true
                columns.addAll(
                        TableColumn<Pair<Plate, IntegerProperty>, String>(getString(R.string.plate)).apply {
                            setCellValueFactory { expose { it.value.first.id.value }.asProperty() }
                            isEditable = false
                        },
                        TableColumn<Pair<Plate, IntegerProperty>, Int>(getString(R.string.quantity)).apply {
                            setCellValueFactory { it.value.second.asObservable() }
                            cellFactory = forTableColumn<Pair<Plate, IntegerProperty>, Int>(stringConverter({ it.toIntOrNull() ?: 0 }))
                            setOnEditCommit { event -> event.rowValue.second.set(event.newValue) }
                        }
                )
            } col 1 row 4
        }
        button(getString(R.string.add), HELP).addConsumedEventFilter(ACTION) {
            choiceDialog(expose { Plate.all().toList() }).showAndWait().ifPresent { plate ->
                tableView.items.add(Pair(plate, 0.asMutableProperty()))
            }
        }
        button(getString(R.string.delete), HELP_2).apply {
            disableProperty() bind tableView.selectionModel.selectedItemProperty().isNull
            addConsumedEventFilter(ACTION) {
                tableView.items.remove(tableView.selectionModel.selectedItem)
            }
        }
        button(CANCEL)
        button(OK)
        setResultConverter { null }
    }
}*/
