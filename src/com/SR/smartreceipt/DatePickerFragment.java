package com.SR.smartreceipt;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

	EditText a;
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
    
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
    	String date;
    	
    	if ((month + 1)<10 && day < 10)
    		date = year + "-0" + (month + 1) + "-0" + day;
    	else if ((month + 1)<10)
    		date = year + "-0" + (month + 1) + "-" + day;
    	else if (day < 10)
    		date = year + "-" + (month + 1) + "-0" + day;
    	else
    		date = year + "-" + (month + 1) + "-" + day;
    	
    	a.setText(date);
    	this.dismiss();
    }

    public void setView(View v) {
    	a = (EditText) v;
    }

}
