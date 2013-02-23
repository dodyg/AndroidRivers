package com.silverkeytech.android_rivers.creators;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.widget.AutoCompleteTextView;
import org.holoeverywhere.app.Activity;

import java.util.ArrayList;

public class AirportAutoComplete {
    public static AutoCompleteTextView getUI(Activity context, Integer uiId, ArrayList<String> names){
        AutoCompleteTextView completion = (AutoCompleteTextView) context.findViewById(uiId);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, org.holoeverywhere.R.layout.simple_dropdown_item_1line, names);
        completion.setAdapter(adapter);
        completion.setHint("Please enter city and country name");
        return completion;
    }
}
