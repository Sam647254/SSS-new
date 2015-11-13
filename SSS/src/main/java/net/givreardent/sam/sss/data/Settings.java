package net.givreardent.sam.sss.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public final class Settings {
	private static boolean notifications, startInClass, mute;
	private static int examReminder, reminder, language;
	private static String filename = "Settings.json";
	private static boolean isLoaded = false;
	
	private static final int defaultLanguage = -1;
	private static final int defaultExamReminder = 30;
	private static final int defaultReminder = 20;
	
	private static final String JSONNotifications = "Notifications?",
			JSONStartInClass = "Start in class?",
			JSONMute = "Mute?",
			JSONExamReminder = "Exam reminder",
			JSONReminder = "Reminder",
			JSONLanguage = "Language";
	
	private Settings() {
		
	}
	
	private static void loadSettings(Context context) {
		BufferedReader reader = null;
		try {
			InputStream in = context.openFileInput(filename);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder JSONString = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				JSONString.append(line);
			}
			JSONObject json = (JSONObject) new JSONTokener(JSONString.toString()).nextValue();
			notifications = json.getBoolean(JSONNotifications);
			startInClass = json.getBoolean(JSONStartInClass);
			mute = json.getBoolean(JSONMute);
			examReminder = json.getInt(JSONExamReminder);
			reminder = json.getInt(JSONReminder);
			language = json.getInt(JSONLanguage);
		} catch (IOException | JSONException e) {
			Log.e("tag", "Error loading settings:", e);
			notifications = true;
			startInClass = true;
			mute = true;
			examReminder = defaultExamReminder;
			reminder = defaultReminder;
			language = defaultLanguage;
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					
				}
			isLoaded = true;
		}
	}
	
	public static boolean saveSettings(Context context) {
		JSONObject json = new JSONObject();
		Writer writer = null;
		try {
			json.put(JSONNotifications, notifications);
			json.put(JSONMute, mute);
			json.put(JSONStartInClass, startInClass);
			json.put(JSONExamReminder, examReminder);
			json.put(JSONReminder, reminder);
			json.put(JSONLanguage, language);
			OutputStream out = context.openFileOutput(filename, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(json.toString());
			return true;
		} catch (JSONException | IOException e) {
			return false;
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					Log.wtf("WTF", "Error closing output stream?!?!?!?!?!", e);
				}
		}
	}

	public static boolean areNotificationsEnabled(Context context) {
		if (!isLoaded)
			loadSettings(context);
		return notifications;
	}

	public static void setNotificationsEnabled(boolean notifications) {
		Settings.notifications = notifications;
	}

	public static boolean startsInClass(Context context) {
		if (!isLoaded)
			loadSettings(context);
		return startInClass;
	}

	public static void setStartsInClass(boolean startInClass) {
		Settings.startInClass = startInClass;
	}

	public static boolean mutes(Context context) {
		if (!isLoaded)
			loadSettings(context);
		return mute;
	}

	public static void setMute(boolean mute) {
		Settings.mute = mute;
	}

	public static int getExamReminder(Context context) {
		if (!isLoaded)
			loadSettings(context);
		return examReminder;
	}

	public static void setExamReminder(int examReminder) {
		Settings.examReminder = examReminder;
	}

	public static int getReminder(Context context) {
		if (!isLoaded)
			loadSettings(context);
		return reminder;
	}

	public static void setReminder(int reminder) {
		Settings.reminder = reminder;
	}

	public static int getLanguage(Context context) {
		if (!isLoaded)
			loadSettings(context);
		return language;
	}

	public static void setLanguage(int language) {
		Settings.language = language;
	}
	
}
