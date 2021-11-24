package org.easyrpg.player;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import org.easyrpg.player.settings.SettingsManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Helper {
	/**
	 * Converts density independent pixel to real screen pixel. 160 dip = 1 inch
	 * ~ 2.5 cm
	 *
	 * @param dipValue
	 *            dip
	 * @return pixel
	 */
	public static int getPixels(Resources r, double dipValue) {
		int dValue = (int) dipValue;
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dValue, r.getDisplayMetrics());
		return px;
	}

	public static int getPixels(View v, double dipValue) {
		return getPixels(v.getResources(), dipValue);
	}

	public static int getPixels(Activity v, double dipValue) {
		return getPixels(v.getResources(), dipValue);
	}

	/**
	 * Moves a view to a screen position. Position is from 0 to 1 and converted
	 * to screen pixel. Alignment is top left.
	 *
	 * @param view
	 *            View to move
	 * @param x
	 *            X position from 0 to 1
	 * @param y
	 *            Y position from 0 to 1
	 */
	public static void setLayoutPosition(Activity a, View view, double x, double y) {
		DisplayMetrics displayMetrics = a.getResources().getDisplayMetrics();
		float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
		float screenHeightDp = displayMetrics.heightPixels / displayMetrics.density;

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		params.leftMargin = Helper.getPixels(a, screenWidthDp * x);
		params.topMargin = Helper.getPixels(a, screenHeightDp * y);

		view.setLayoutParams(params);
	}

	/**
	 * Moves a view to a screen position. Position is from 0 to 1 and converted
	 * to screen pixel. Alignment is top right.
	 *
	 * @param view
	 *            View to move
	 * @param x
	 *            X position from 0 to 1
	 * @param y
	 *            Y position from 0 to 1
	 */
	public static void setLayoutPositionRight(Activity a, View view, double x, double y) {
		DisplayMetrics displayMetrics = a.getResources().getDisplayMetrics();
		float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
		float screenHeightDp = displayMetrics.heightPixels / displayMetrics.density;

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
		params.rightMargin = Helper.getPixels(a, screenWidthDp * x);
		params.topMargin = Helper.getPixels(a, screenHeightDp * y);
		view.setLayoutParams(params);
	}

	public static Paint getUIPainter() {
		Paint uiPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		uiPaint.setColor(Color.argb(128, 255, 255, 255));
		uiPaint.setStyle(Style.STROKE);
		uiPaint.setStrokeWidth((float) 3.0);
		return uiPaint;
	}

	public static void showWrongAPIVersion(Context context) {
		Toast.makeText(context, "Not avaible on this API", Toast.LENGTH_SHORT).show();
	}

	public static JSONObject readJSON(String contentFile) {
		try {
			// Parse the JSON
			JSONObject jso = new JSONObject(contentFile);
			return jso;
		} catch (JSONException e) {
			Log.e("JSO reading", "Error parsing a JSO file : " + e.getMessage());
		}

		return null;
	}

	public static JSONObject readJSONFile(String path) {
		String file = new String(), tmp;
		try {
			// Read the file
			BufferedReader bf = new BufferedReader(new FileReader(new File(path)));
			while ((tmp = bf.readLine()) != null) {
				file += tmp;
			}
			bf.close();

			// Parse the JSON
			JSONObject jso = new JSONObject(file);
			return jso;
		} catch (JSONException e) {
			Log.e("JSO reading", "Error parsing the JSO file " + path + "\n" + e.getMessage());
		} catch (IOException e) {
			Log.e("JSO reading", "Error reading the file " + path + "\n" + e.getMessage());
		}

		return null;
	}

	public static String readInternalFileContent(Context content, String fileName) {
		String file = new String(), tmp;
		try {
			// Read the file
			BufferedReader bf = new BufferedReader(new InputStreamReader(content.openFileInput(fileName)));
			while ((tmp = bf.readLine()) != null) {
				file += tmp;
			}
			bf.close();
		} catch (IOException e) {
			Log.e("JSO reading", "Error reading the file " + fileName + "\n" + e.getMessage());
		}
		return file;
	}

	/** Create RTP folders and .nomedia file in the games folder */
	public static void createEasyRPGDirectories(DocumentFile gamesFolder){
		// RTP folder
        DocumentFile RTPFolder = createFolder(gamesFolder, "RTP");
        createFolder(RTPFolder, "2000");
        createFolder(RTPFolder, "2003");

        // Save the RTP folder in Settings
        SettingsManager.setRtpFolder(RTPFolder);

        // The .nomedia file (avoid media app to scan games and RTP's folders)
        if (gamesFolder.findFile(".nomedia") == null) {
            gamesFolder.createFile("", ".nomedia");
        }
	}

	private static DocumentFile createFolder(DocumentFile location, String folderName) {
        DocumentFile folder = location.findFile(folderName);
        if (folder == null || !folder.isDirectory()) {
            folder = location.createDirectory(folderName);
        }

        if (folder == null) {
            Log.e("EasyRPG", "Problem creating folder " + folderName);
        }

        return folder;
    }
}
