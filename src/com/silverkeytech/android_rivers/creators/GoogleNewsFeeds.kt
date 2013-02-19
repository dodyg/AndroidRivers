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

package com.silverkeytech.android_rivers.creators

import android.view.View
import org.holoeverywhere.ArrayAdapter
import android.widget.EditText
import com.silverkeytech.android_rivers.R
import com.silverkeytech.android_rivers.dlgClickListener
import com.silverkeytech.android_rivers.isNullOrEmpty
import java.util.HashMap
import org.holoeverywhere.app.Activity
import org.holoeverywhere.app.AlertDialog
import org.holoeverywhere.widget.Spinner
import android.util.Log

data class EditionAndLanguage(val edition : String, val lang : String)

//Create a popup dialog with one text for url submission. Then provide the given url via a callback called 'action'
public fun createGoogleNewsCreatorDialog(context: Activity, onOK : (url : String, title : String, lang : String) -> Unit): AlertDialog {
    val dlg: View = context.getLayoutInflater()!!.inflate(R.layout.dialog_gnews_creator, null)!!

    //take care of color
    val dialog = AlertDialog.Builder(context)
    dialog.setView(dlg)
    dialog.setTitle("Google News Search")

    val searchTerm = dlg.findViewById(R.id.dialog_gnews_creator_search)!! as EditText
    searchTerm.setHint("Optional search term")
    searchTerm.setFocusableInTouchMode(true)
    searchTerm.requestFocus()

    val regionList = dlg.findViewById(R.id.dialog_gnews_creator_region)!! as Spinner

    val maps = getEditionsAndLanguages()
    val listName = maps.iterator().map { x -> x.key }.toArrayList<String>().sort()


    val  adapter = ArrayAdapter<String>(context, org.holoeverywhere.R.layout.simple_spinner_item, listName);
    adapter.setDropDownViewResource(org.holoeverywhere.R.layout.simple_spinner_dropdown_item);
    regionList.setAdapter(adapter);

    dialog.setPositiveButton(context.getString(R.string.ok), dlgClickListener {
        dlg, idx ->
            val position = regionList.getSelectedItemPosition()
            val key = listName.get(position)
            val info = maps[key]!!
            var rss = "https://news.google.com/news/feeds?cf=all&ned=${info.edition}&hl=${info.lang}&output=rss"
            val search = searchTerm.getText()?.toString()
            if (!search.isNullOrEmpty()){
                rss +="&q=${java.net.URLEncoder.encode(search!!, "UTF-8")}"
            }

            val title = if (search.isNullOrEmpty()) key else search!!

            Log.d("googleNewsFeedDialog","Rss - $rss")
        onOK(rss, title, info.lang)
    })

    dialog.setNegativeButton(context.getString(R.string.cancel), dlgClickListener {
        dlg, idx ->
            dlg?.dismiss()
    })

    val createdDialog = dialog.create()!!
    return createdDialog
}

fun getEditionsAndLanguages() : HashMap<String, EditionAndLanguage>{
    return hashMapOf(
            "Argentina" to EditionAndLanguage("es_ar", "es"),
            "België" to EditionAndLanguage("nl_be", "nl"),
            "Brasil" to EditionAndLanguage("pt-BR", "pt-BR"),
            "Česká republika" to EditionAndLanguage("cs_cz", "cs"),
            "Maroc" to EditionAndLanguage("fr_ma", "fr"),
            "إصدار العالم العربي" to EditionAndLanguage("ar_me", "ar"),
            "US" to EditionAndLanguage("us", "en")
    )
}