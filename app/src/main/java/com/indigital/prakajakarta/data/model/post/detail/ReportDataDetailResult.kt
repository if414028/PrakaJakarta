package com.indigital.prakajakarta.data.model.post.detail

import com.indigital.prakajakarta.data.model.Status

class ReportDataDetailResult : Status() {
    private var data: ReportDetail? = null

    fun getData(): ReportDetail? {
        return data
    }

    fun setData(data: ReportDetail?) {
        this.data = data
    }
}