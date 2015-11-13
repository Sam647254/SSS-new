package net.givreardent.sam.sss.JSONIO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import net.givreardent.sam.sss.data.courses.Term;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;

public class TermJSONSerializer {
	private Context mContext;

	public TermJSONSerializer(Context c) {
		mContext = c;
	}

	public ArrayList<Term> loadTerms(String filename) throws IOException, JSONException {
		ArrayList<Term> terms = new ArrayList<Term>();
		BufferedReader reader = null;
		try {
			InputStream in = mContext.openFileInput(filename);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			for (int i = 0; i < array.length(); i++) {
				terms.add(new Term(array.getJSONObject(i), mContext));
			}
		} catch (FileNotFoundException e) {
			
		} finally {
			if (reader != null)
				reader.close();
		}
		return terms;
	}

	public void saveTerm(ArrayList<Term> terms, String filename)
			throws IOException, JSONException {
		JSONArray array = new JSONArray();
		for (Term t : terms) {
			array.put(t.toJSON());
		}
		Writer writer = null;
		try {
			OutputStream out = mContext.openFileOutput(filename,
					Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}
