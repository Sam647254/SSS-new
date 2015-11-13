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

import net.givreardent.sam.sss.data.journals.Journal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;

public class JournalJSONSerializer {
	private Context mContext;
	private String mFilename;
	
	public JournalJSONSerializer(Context c, String f) {
		mContext = c;
		mFilename = f;
	}
	
	public ArrayList<Journal> loadJournals() throws IOException, JSONException {
		ArrayList<Journal> journals = new ArrayList<Journal>();
		Log.d("Journal List Fragment", mFilename);
		BufferedReader reader = null;
		try {
			InputStream in = mContext.openFileInput(mFilename);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			for (int i = 0; i < array.length() ; i++) {
				journals.add(new Journal(array.getJSONObject(i)));
			}
		} catch (FileNotFoundException e) {
			
		} finally {
			if (reader != null)
				reader.close();
		}
		return journals;
	}
	
	public void saveJournals(ArrayList<Journal> journals) throws JSONException, IOException {
		JSONArray array = new JSONArray();
		for (Journal j: journals) {
			array.put(j.toJSON());
		}
		
		Writer writer = null;
		try {
			OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}
