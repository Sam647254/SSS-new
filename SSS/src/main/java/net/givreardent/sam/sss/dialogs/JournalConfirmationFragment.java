package net.givreardent.sam.sss.dialogs;

import net.givreardent.sam.sss.JournalEntryFragment;
import net.givreardent.sam.sss.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

public class JournalConfirmationFragment extends DialogFragment {
	public static final String extra_answer = "response";
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_journal_save, null);
		
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.save_confirmation)
				.setView(v)
				.setPositiveButton(R.string.journal_save_yes, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sendResult(JournalEntryFragment.yes);
					}
				})
				.setNegativeButton(R.string.journal_save_no, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sendResult(JournalEntryFragment.no);
					}
				})
				.setNeutralButton(R.string.journal_save_cancel, null).create();
	}
	
	private void sendResult(int resultCode) {
		if (getTargetFragment() == null)
			return;
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, null);
	}
}
