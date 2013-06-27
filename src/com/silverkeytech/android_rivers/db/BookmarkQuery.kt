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

package com.silverkeytech.android_rivers.db

import com.j256.ormlite.dao.Dao

public class BookmarkQuery(private val dao: Dao<Bookmark, out Int?>){
    fun byKinds(vararg kinds: BookmarkKind, sortByTitleOrder: SortingOrder): QueryMany<Bookmark> {
        try{
            var q = dao.queryBuilder()!!

            when(kinds.size){
                1 -> q.where()!!
                        .eq(BOOKMARK_KIND, kinds[0].toString())!!
                2 -> q.where()!!
                        .eq(BOOKMARK_KIND, kinds[0].toString())!!
                        .or()!!
                        .eq(BOOKMARK_KIND, kinds[1].toString())!!
                3 -> q.where()!!
                        .eq(BOOKMARK_KIND, kinds[0].toString())!!
                        .or()!!
                        .eq(BOOKMARK_KIND, kinds[1].toString())!!
                        .or()!!
                        .eq(BOOKMARK_KIND, kinds[2].toString())!!
                else -> {
                    throw Exception("Kinds parameter only accept maximum 3 conditions")
                }
            }



            if (sortByTitleOrder == SortingOrder.ASC)
                q.orderByRaw("$BOOKMARK_TITLE COLLATE NOCASE ASC;")
            else if (sortByTitleOrder == SortingOrder.DESC)
                q.orderByRaw("$BOOKMARK_TITLE COLLATE NOCASE DESC;")

            return QueryMany(dao.query(q.prepare()))
        }
        catch(e: Exception){
            return QueryMany<Bookmark>(null, e)
        }
    }


    fun byCollectionId(collectionId: Int): QueryMany<Bookmark> {
        try{
            var q = dao.queryBuilder()!!
                    .where()!!
                    .eq(BOOKMARK_COLLECTION, collectionId)!!
                    .prepare()

            return QueryMany(dao.query(q))
        }
        catch(e: Exception){
            return QueryMany<Bookmark>(null, e)
        }
    }

    fun all(): QueryMany<Bookmark> {
        try{
            return QueryMany(dao.queryForAll())
        }
        catch(e: Exception){
            return QueryMany<Bookmark>(null, e)
        }
    }

    fun byUrl(url: String): QueryOne<Bookmark> {
        try
        {
            var q = dao.queryBuilder()!!
                    .where()!!
                    .eq(BOOKMARK_URL, url)!!
                    .prepare()

            return QueryOne(dao.queryForFirst(q))
        }
        catch(e: Exception){
            return QueryOne<Bookmark>(null, e)
        }
    }

    fun byId(id: Int): QueryOne<Bookmark> {
        try
        {
            var q = dao.queryBuilder()!!
                    .where()!!
                    .eq(BOOKMARK_ID, id)!!
                    .prepare()

            return QueryOne(dao.queryForFirst(q))
        }
        catch(e: Exception){
            return QueryOne<Bookmark>(null, e)
        }
    }
}
