package net.givreardent.sam.sss.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.TimePicker;

public class ScrollProofTimePicker extends TimePicker {

	public ScrollProofTimePicker(Context context) {
		super(context);
	}

	public ScrollProofTimePicker(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScrollProofTimePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
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
