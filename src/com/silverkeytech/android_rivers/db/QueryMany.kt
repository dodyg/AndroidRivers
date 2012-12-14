package com.silverkeytech.android_rivers.db

//hold values for query many
public class QueryMany<T>(private val items : List<T>?, private val e : Exception? = null){
    public val exist: Boolean = items != null && items.count() > 0  && e == null
    public val values : List<T>? = items
    public val exception : Exception? = e
}