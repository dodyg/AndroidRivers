package com.silverkeytech.android_rivers

public data class Result<T: Any>(p1: T?, p2: Exception? = null){
    class object{
        //return True result
        fun right<T: Any>(val value: T?): Result<T> {
            return Result<T>(value)
        }

        //return false result
        fun wrong<T: Any>(val exception: Exception?): Result<T> {
            return Result<T>(null, exception)
        }
    }

    public val value: T? = p1
    public val exception: Exception? = p2

    public fun isTrue(): Boolean {
        return exception == null
    }

    public fun isFalse(): Boolean {
        return exception != null
    }
}