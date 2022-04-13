package com.djessyczaplicki.groupcalendar.data.remote.model

class FCMBody(
    multicast_id: Int,
    success: Int,
    failure: Int,
    canonical_ids: Int,
    results: ArrayList<Any>
) {
    var multicast_id: Long
        private set
    var success: Int
    var failure: Int
    var canonical_ids: Int
    var results = ArrayList<Any>()

    fun setMulticast_id(multicast_id: Int) {
        this.multicast_id = multicast_id.toLong()
    }

    init {
        this.multicast_id = multicast_id.toLong()
        this.success = success
        this.failure = failure
        this.canonical_ids = canonical_ids
        this.results = results
    }

}
