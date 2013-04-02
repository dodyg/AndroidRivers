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

package com.silverkeytech.android_rivers.creators;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.AutoCompleteTextView;

import java.util.ArrayList;

public class CityAutoComplete {
    public static AutoCompleteTextView getUI(Activity context, Integer uiId, ArrayList<String> names) {
        AutoCompleteTextView completion = (AutoCompleteTextView) context.findViewById(uiId);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, org.holoeverywhere.R.layout.simple_dropdown_item_1line, names);
        completion.setAdapter(adapter);
        completion.setHint("Please enter city name");
        return completion;
    }
}

