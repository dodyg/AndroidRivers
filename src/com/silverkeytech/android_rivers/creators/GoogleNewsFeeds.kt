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