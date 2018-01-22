package com.wijayaprinting.db.schema

import com.wijayaprinting.db.dao.OffsetOrder

object OffsetOrders : Orders<OffsetOrder, OffsetOrders>(OffsetOrder::class, "print")