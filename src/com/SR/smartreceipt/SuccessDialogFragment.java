package com.SR.smartreceipt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
* Fragment that displays a dialog for a success message
* 
* @author Παναγιώτης Κουτσαυτίκης 8100062
*/
public class SuccessDialogFragment extends DialogFragment {
	
	// the message to be displayed
	String message;
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        
        // Create the AlertDialog object and return it
        return builder.create();
    }
    
    
	/**
	 * sets the message that takes from the Activity
	 * 
	 * @param m  the message to be displayed
	 */
    public void setMessage (String m){
    	message = m;
    }

}
