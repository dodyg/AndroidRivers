package com.silverkeytech.news_engine

public var log: ((String, String) -> Unit) = { x, y -> }
public var scrubHtml: ( (String?) -> String) = { x -> "" }
