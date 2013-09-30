package com.SR.smartreceipt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
* Fragment that displays a dialog for an error message
* 
* @author Panagiotis Koutsaftikis
*/
public class InputErrorDialogFragment extends DialogFragment {
    
	// the message to be displayed
	String message;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.dismiss();
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }

	/**
	 * sets the message that takes from the Activity
	 * 
	 * @param m  the message to be displayed
	 */
	public void setMessage (String m) {
		message = m;
	}
}
