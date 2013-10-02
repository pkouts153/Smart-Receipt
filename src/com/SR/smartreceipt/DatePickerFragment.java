package com.SR.smartreceipt;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

/**
* Fragment that displays a dialog for date selection
* 
* @author Παναγιώτης Κουτσαυτίκης 8100062
*/
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

	// the EditText the user clicked and launched the dialog
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
    
    /**
     * Gets the selection of the user and sets the text of the EditText accordingly
     */
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

    /**
     * @param v the view (EditView) the user clicked
     */
    public void setView(View v) {
    	a = (EditText) v;
    }

}
