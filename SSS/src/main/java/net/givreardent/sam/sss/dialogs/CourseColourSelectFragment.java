package net.givreardent.sam.sss.dialogs;

import net.givreardent.sam.sss.CourseCreateFragment;
import net.givreardent.sam.sss.R;
import net.givreardent.sam.sss.data.courses.Course;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

public class CourseColourSelectFragment extends DialogFragment {
	
	@TargetApi(11)
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View.OnClickListener tileListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ColorDrawable cd = (ColorDrawable) v.getBackground();
				sendResult(cd.getColor());
			}
		};
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.dialog_colour_picker, null);
		TableLayout tableLayout = (TableLayout) v
				.findViewById(R.id.colour_picker_tableLayout);
		TableRow row = (TableRow) tableLayout.getChildAt(0);
		for (int i = 0; i<row.getChildCount();i++) {
			View tile = row.getChildAt(i);
			tile.setBackgroundColor(Course.blue[i]);
			tile.setOnClickListener(tileListener);
		}
		row = (TableRow) tableLayout.getChildAt(1);
		for (int i = 0; i<row.getChildCount();i++) {
			View tile = row.getChildAt(i);
			tile.setBackgroundColor(Course.red[i]);
			tile.setOnClickListener(tileListener);
		}
		row = (TableRow) tableLayout.getChildAt(2);
		for (int i = 0; i<row.getChildCount();i++) {
			View tile = row.getChildAt(i);
			tile.setBackgroundColor(Course.green[i]);
			tile.setOnClickListener(tileListener);
		}
		row = (TableRow) tableLayout.getChildAt(3);
		for (int i = 0; i<row.getChildCount();i++) {
			View tile = row.getChildAt(i);
			tile.setBackgroundColor(Course.orange[i]);
			tile.setOnClickListener(tileListener);
		}
		row = (TableRow) tableLayout.getChildAt(4);
		for (int i = 0; i<row.getChildCount();i++) {
			View tile = row.getChildAt(i);
			tile.setBackgroundColor(Course.purple[i]);
			tile.setOnClickListener(tileListener);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		AlertDialog dialog = builder.create();
		dialog.setView(v);
		dialog.setTitle(R.string.colour_choose_label);
		return dialog;
	}

	private void sendResult(int colourChosen) {
		if (getTargetFragment() == null) return;
		getTargetFragment().onActivityResult(CourseCreateFragment.requestColour, colourChosen, null);
		dismiss();
	}
}
