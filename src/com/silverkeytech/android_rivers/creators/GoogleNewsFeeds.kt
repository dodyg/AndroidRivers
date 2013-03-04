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
import java.util.TreeMap
import org.holoeverywhere.app.Activity
import org.holoeverywhere.app.AlertDialog
import org.holoeverywhere.widget.Spinner
import android.util.Log

data class EditionAndLanguage(val edition : String, val lang : String)


fun getEditionsAndLanguages() : TreeMap<String, EditionAndLanguage>{
    val mp = TreeMap<String, EditionAndLanguage>()
    mp.putAll(
            "Argentina" to EditionAndLanguage("es_ar", "es"),
            "Australia" to EditionAndLanguage("au", "en"),
            "België" to EditionAndLanguage("nl_be", "nl"),
            "Belgique" to EditionAndLanguage("fr_be", "fr"),
            "Botswana" to EditionAndLanguage("en_bw", "en"),
            "Brasil" to EditionAndLanguage("pt-BR", "pt-BR"),
            "Canada English" to EditionAndLanguage("ca", "en"),
            "Canada French" to EditionAndLanguage("fr_ca", "fr"),
            "Chile" to EditionAndLanguage("es_cl", "es"),
            "Colombia" to EditionAndLanguage("es_co", "es"),
            "Cuba" to EditionAndLanguage("es_cu", "es"),
            "Česká republika" to EditionAndLanguage("cs_cz", "cs"),
            "Deutschland" to EditionAndLanguage("de", "de"),
            "España" to EditionAndLanguage("es", "es"),
            "Estados Unidos" to EditionAndLanguage("es_us", "es"),
            "Ethiopia" to EditionAndLanguage("en_et", "en"),
            "France" to EditionAndLanguage("fr", "fr"),
            "Ghana" to EditionAndLanguage("en_gh", "en"),
            "India" to EditionAndLanguage("In", "en"),
            "Ireland" to EditionAndLanguage("en_ie", "en"),
            "Israel English" to EditionAndLanguage("en_il", "en"),
            "Italia" to EditionAndLanguage("it", "it"),
            "Kenya"to EditionAndLanguage("en_ke", "en"),
            "Magyarország" to EditionAndLanguage("hu_hu", "hu"),
            "Malaysia" to EditionAndLanguage("en_my", "en"),
            "Maroc" to EditionAndLanguage("fr_ma", "fr"),
            "México" to EditionAndLanguage("es_mx", "es"),
            "Namibia" to EditionAndLanguage("en_na", "en"),
            "Nederland" to EditionAndLanguage("nl_nl", "nl"),
            "New Zealand" to EditionAndLanguage("nz", "en"),
            "Nigeria" to EditionAndLanguage("en_ng", "en"),
            "Norge" to EditionAndLanguage("no_no", "no"),
            "Österreich" to EditionAndLanguage("de_at", "de"),
            "Pakistan" to EditionAndLanguage("en_pk", "en"),
            "Perú" to EditionAndLanguage("es_pe", "es"),
            "Philippines" to EditionAndLanguage("en_ph", "en"),
            "Polska" to EditionAndLanguage("pl_pl", "pl"),
            "Portugal" to EditionAndLanguage("pt-PT_pt", "pt-PT"),
            "Schweiz" to EditionAndLanguage("de_ch", "de"),
            "Sénégal"to EditionAndLanguage("fr_sn", "fr"),
            "Singapore" to EditionAndLanguage("en_sg", "en"),
            "South Africa" to EditionAndLanguage("en_za", "en"),
            "Suisse" to EditionAndLanguage("fr_ch", "fr"),
            "Sverige" to EditionAndLanguage("sv_se", "sv"),
            "Tanzania" to EditionAndLanguage("en_tz", "en"),
            "Türkiye" to EditionAndLanguage("tr_tr", "tr"),
            "U.K." to EditionAndLanguage("uk", "en"),
            "U.S." to EditionAndLanguage("us", "en"),
            "Uganda" to EditionAndLanguage("en_ug", "en"),
            "Venezuela" to EditionAndLanguage("es_ve", "es"),
            "Việt Nam (Vietnam)" to EditionAndLanguage("vi_vn", "vi"),
            "Zimbabwe" to EditionAndLanguage("en_zw", "en"),
            "Ελλάδα (Greece)" to EditionAndLanguage("el_gr", "el"),
            "Россия (Russia)" to EditionAndLanguage("ru_ru", "ru"),
            "Србија (Serbia)" to EditionAndLanguage("sr_rs", "sr"),
            "Украина / русский (Ukraine)" to EditionAndLanguage("ru_ua", "ru"),
            "Україна / українська (Ukraine)" to EditionAndLanguage("uk_ua", "uk"),
            "ישראל (Israel)" to EditionAndLanguage("iw_il", "iw"),
            "الإمارات (UAE)" to EditionAndLanguage("ar_ae", "ar"),
            "السعودية (KSA)" to EditionAndLanguage("ar_sa", "ar"),
            "إصدار العالم العربي" to EditionAndLanguage("ar_me", "ar"),
            "لبنان (Lebanon)" to EditionAndLanguage("ar_lb", "ar"),
            "مصر (Egypt)" to EditionAndLanguage("ar_eg", "ar"),
            "हिन्दी (India)" to EditionAndLanguage("hi_in", "hi"),
            "தமிழ்(India)" to EditionAndLanguage("ta_in", "ta"),
            "తెలుగు (India)" to EditionAndLanguage("te_in", "te"),
            "മലയാളം (India)" to EditionAndLanguage("ml_in", "ml"),
            "한국 (Korea)" to EditionAndLanguage("kr", "kr"),
            "中国 (China)" to EditionAndLanguage("cn", "zh-CN"),
            "台灣 (Taiwan)" to EditionAndLanguage("tw", "zh-TW"),
            //"日本 (Japan)" to EditionAndLanguage("jp", "jp"),
            "香港 (Hong Kong)" to EditionAndLanguage("hk", "zh-TW")
    )

    return mp
}