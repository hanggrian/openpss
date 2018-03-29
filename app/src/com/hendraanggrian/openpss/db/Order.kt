package com.hendraanggrian.openpss.db

interface Order : Totaled {

    var title: String
    var qty: Int
}