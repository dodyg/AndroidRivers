package com.silverkeytech.android_rivers.db

//hold one value
public class QueryOne<T>(private val item : T, private val e : Exception? = null){
    public val exists: Boolean = item != null && e == null
    public val value : T = item
    public val exception : Exception? = e
}