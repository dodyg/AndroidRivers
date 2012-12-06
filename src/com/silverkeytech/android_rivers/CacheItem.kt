package com.silverkeytech.android_rivers

fun futureTimeFromNowInMilies(seconds: Int): Long {
    return System.currentTimeMillis() + (seconds.toLong() * 1000.toLong())
}

public data class CacheItem<T: Any>(val item: T){
    public var expireIn: Long = futureTimeFromNowInMilies(60 * 10)

    public fun setExpireInSecondsFromNow(seconds: Int) {
        expireIn = futureTimeFromNowInMilies(seconds)
    }

    public fun setExpireInMinutesFromNow(minutes: Int) {
        setExpireInSecondsFromNow(minutes * 60)
    }

    public val isExpired: Boolean
        get() = System.currentTimeMillis() > expireIn

}