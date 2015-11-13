package net.givreardent.sam.sss.dialogs;

import net.givreardent.sam.sss.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DeleteTaskFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.task_delete_confirm_title);
		builder.setMessage(R.string.task_delete_confirm_message);
		builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (getTargetFragment() == null) return;
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
			}
		});
		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (getTargetFragment() == null) return;
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
			}
		});
		return builder.create();
	}
}
