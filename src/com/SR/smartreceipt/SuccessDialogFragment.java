package com.SR.smartreceipt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;

public class SuccessDialogFragment extends DialogFragment {
	String message;
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        
        // Create the AlertDialog object and return it
        return builder.create();
    }
    
    public void setMessage (String m){
    	message = m;
    }

}
