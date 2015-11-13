package net.givreardent.sam.sss.data.journals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import net.givreardent.sam.sss.JSONIO.JournalJSONSerializer;
import android.content.Context;
import android.util.Log;

public class JournalList {
	
	private static final String tag = "Journal List";
	private String filename = "Journal entries for ";
	private static JournalList sJournalList;
	private Context mAppContext;
	private ArrayList<Journal> mJournals;
	private int year;
	private JournalJSONSerializer mSerializer;
	
	private JournalList(Context appContext) {
		mAppContext = appContext;
		year = Calendar.getInstance().get(Calendar.YEAR);
		filename += year + ".json";
		mSerializer = new JournalJSONSerializer(mAppContext, filename);
		try {
			mJournals = mSerializer.loadJournals();
		} catch (Exception e) {
			mJournals = new ArrayList<Journal>();
			Log.e(tag, "Error loading crimes: ", e);
		}
	}
	
	private JournalList(Context appContext, int year) {
		mAppContext = appContext;
		mJournals = new ArrayList<Journal>();
		this.year = year;
		filename += year + ".json";
		mSerializer = new JournalJSONSerializer(mAppContext, filename);
		try {
			mJournals = mSerializer.loadJournals();
		} catch (Exception e) {
			mJournals = new ArrayList<Journal>();
			Log.e(tag, "Error loading crimes: ", e);
		}
	}
	
	public static JournalList get(Context c) {
		if (sJournalList == null) {
			sJournalList = new JournalList(c.getApplicationContext());
		}
		return sJournalList;
	}
	
	public static JournalList get(Context c, int y) {
		if (sJournalList == null) {
			sJournalList = new JournalList(c.getApplicationContext());
		}
		if (sJournalList.getYear() != y) {
			sJournalList.saveJournals();
			sJournalList = new JournalList(c.getApplicationContext(), y);
		}
		return sJournalList;
	}
	
	public void addEntry(Journal entry) {
		mJournals.add(entry);
	}
	
	public ArrayList<Journal> getJournals() {
		return mJournals;
	}
	
	public int getYear() {
		return year;
	}
	
	public Journal getEntry(UUID ID) {
		for (Journal j : mJournals) {
			if (j.getID().equals(ID))
				return j;
		}
		return null;
	}

	public boolean saveJournals() {
		try {
			mSerializer.saveJournals(mJournals);
			Log.d(tag, "Journals of "+year+" saved to file.");
			return true;
		} catch (Exception e) {
			Log.e(tag, "Error saving journals: ", e);
			return false;
		}
	}
}
