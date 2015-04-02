/*
Android Rivers is an app to read and discover news using RiverJs, RSS and OPML format.
Copyright (C) 2012 Dody Gunawinata (dodyg@silverkeytech.com)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package com.silverkeytech.news_engine

public class None(){
}

public data class Result<T: Any>(p1: T?, p2: Exception? = null){
    companion object{
        //return True result
        fun right<T: Any>(value: T?): Result<T> {
            return Result<T>(value)
        }

        //return false result
        fun wrong<T: Any>(exception: Exception?): Result<T> {
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