package com.bookpal.utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.bookpal.R;

/**
 * Created by asif on 13/7/16.
 */
public class AlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                // Set Dialog Icon
                //.setIcon(R.drawable.alert)
                // Set Dialog Title
                .setTitle("Congrats !!!")
                // Set Dialog Message
                .setMessage("Your book added successfully. You may add another book.")

                // Positive button
                /*.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Utility.openAppInGooglePlay(getActivity(), "com.mohenmov");
                        int count = SharedPreference.getInt(getActivity(), "checkDialogVisibility");
                        SharedPreference.setInt(getActivity(), "checkDialogVisibility", count + 1);
                    }
                })

                // Negative Button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int count = SharedPreference.getInt(getActivity(), "checkDialogVisibility");
                        SharedPreference.setInt(getActivity(), "checkDialogVisibility", count + 1);
                        dismiss();
                    }
                })*/
                .setNeutralButton(R.string.label_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .create();
    }
}
