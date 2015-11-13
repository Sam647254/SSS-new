package net.givreardent.sam.sss.data.courses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import net.givreardent.sam.sss.JSONIO.TermJSONSerializer;

import org.json.JSONException;

import android.content.Context;
import android.util.Log;

public class TermList {
	private static TermList sTermList;
	private Context mAppContext;
	private ArrayList<Term> mTerms;
	private TermJSONSerializer mSerializer;

	private TermList(Context c) {
		mAppContext = c;
		mSerializer = new TermJSONSerializer(mAppContext);
		try {
			mTerms = mSerializer.loadTerms("Terms.json");
		} catch (Exception e) {
			mTerms = new ArrayList<Term>();
			Log.e("tag", "Error loading terms.", e);
		}
	}

	public static TermList get(Context c) {
		if (sTermList == null) {
			sTermList = new TermList(c.getApplicationContext());
		}
		return sTermList;
	}

	public ArrayList<Term> getTerms() {
		return mTerms;
	}

	public void addTerm(Term term) {
		mTerms.add(term);
	}
	
	public void removeTerm(Term term) {
		mTerms.remove(term);
	}

	public Term getTerm(UUID ID) {
		for (Term t : mTerms) {
			if (t.getID().equals(ID))
				return t;
		}
		return null;
	}

	public void saveTerms() {
		try {
			mSerializer.saveTerm(mTerms, "Terms.json");
		} catch (IOException | JSONException e) {
			Log.e("tag", "Error saving terms", e);
		}
	}
}
