package com.example.da_proyecto

import java.net.URLEncoder

object LinkHelper {
    fun generadorLink(card: Card): String {
        val monitorUrl = "/App/monitor.html?cardId=${card.id}"
        val encodedMonitorUrl = URLEncoder.encode(monitorUrl, "UTF-8")

        return "https://link-app/index.html?red=$encodedMonitorUrl"
    }
}