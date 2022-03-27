package com.djessyczaplicki.groupcalendar.util

import android.net.Uri
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class DynamicLinksUtil {
    companion object {
        fun generateContentLink(): Uri {
            val baseUrl = Uri.parse("https://groupcalendar.djessyczaplicki.com")
            val domain = "https://groupcalendar.djessyczaplicki.com"

            val link = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setLink(baseUrl)
                .setDomainUriPrefix(domain)
                .setAndroidParameters(DynamicLink.AndroidParameters.Builder("com.djessyczaplicki.groupcalendar").build())
                .buildDynamicLink()

            return link.uri
        }
    }
}