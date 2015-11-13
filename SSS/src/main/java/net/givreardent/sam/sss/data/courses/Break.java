package net.givreardent.sam.sss.data.courses;

import java.util.Calendar;
import java.util.Date;

import net.givreardent.sam.sss.data.TimePeriod;

import org.json.JSONException;
import org.json.JSONObject;

public class Break extends TimePeriod {
	
	private final Term term;
	
	public Break(Term term) {
		this.term = term;
		setEndDate(getEndDate());
	}
	
	public Break(JSONObject json, Term term) throws JSONException {
		super(json);
		this.term = term;
		setEndDate(getEndDate());
	}
	
	@Override
	public void setEndDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		super.setEndDate(calendar.getTime());
	}
	
	public Term getTerm() {
		return term;
	}
}
