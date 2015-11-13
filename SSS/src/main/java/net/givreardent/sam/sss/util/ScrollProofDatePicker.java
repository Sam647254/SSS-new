package net.givreardent.sam.sss.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.DatePicker;

public class ScrollProofDatePicker extends DatePicker {

	public ScrollProofDatePicker(Context context) {
		super(context);
	}

	public ScrollProofDatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollProofDatePicker(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(21)
	public ScrollProofDatePicker(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
			ViewParent p = getParent();
			if (p != null)
				p.requestDisallowInterceptTouchEvent(true);
		}

		return false;
	}

}
