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

package com.silverkeytech.news_engine.syndications.atom

import java.io.InputStream
import com.thebuzzmedia.sjxp.XMLParser
import com.silverkeytech.news_engine.xml.textRule

public class AtomParser{
    public fun parse(input: InputStream, atom: AtomBuilder) {
        var parser = XMLParser<AtomBuilder>(feedId, feedTitle, feedUpdated, feedIcon, feedLogo
        , feedSubtitle)
        parser.parse(input, "UTF-8", atom)
    }
}

val NS = "http://www.w3.org/2005/Atom"

val feedId = textRule<AtomBuilder>("/[$NS]feed/[$NS]id", { text, atom ->
    atom.setId(text)
})

val feedTitle = textRule<AtomBuilder>("/[$NS]feed/[$NS]title", { text, atom ->
    atom.setTitle(text)
})

val feedUpdated = textRule<AtomBuilder>("/[$NS]feed/[$NS]updated", { text, atom ->
    atom.setUpdated(text)
})

val feedIcon = textRule<AtomBuilder>("/[$NS]feed/[$NS]icon", { text , atom ->
    atom.setIcon(text)
})

val feedLogo = textRule<AtomBuilder>("/[$NS]feed/[$NS]logo", { text, atom ->
    atom.setLogo(text)
})

val feedSubtitle = textRule<AtomBuilder>("/[$NS]feed/[$NS]subtitle", { text, atom ->
    atom.setSubtitle(text)
})