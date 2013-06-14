package com.mythicacraft.plugins.mythsentials.Tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.mythicacraft.plugins.mythsentials.Mythsentials;

public class Time {

	public static Mythsentials plugin;

	public Time(Mythsentials plugin) {
		Time.plugin = plugin;
	}

	public static String getTime() {
		Calendar cal = Calendar.getInstance();
		cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.ENGLISH);
		String time = sdf.format(cal.getTime());
		return time;
	}

	public static String getTimeinMills() {
		Calendar cal = Calendar.getInstance();
		cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.ENGLISH);
		String time = sdf.format(cal.getTime());
		return time;
	}

	private static int compareTime(String time) throws ParseException {

		String currentTime = getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.ENGLISH);
		Date date1 = sdf.parse(time);
		Date date2 = sdf.parse(currentTime);

		Long differnceInMills = date2.getTime() - date1.getTime();

		long timeInMinutes = differnceInMills/60000;
		int totalMinutes = (int) timeInMinutes;

		return totalMinutes;
	}

	public static long compareTimeMills(String time) throws ParseException {

		String currentTime = getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.ENGLISH);
		Date date1 = sdf.parse(time);
		Date date2 = sdf.parse(currentTime);

		Long differenceInMills = date2.getTime() - date1.getTime();

		return differenceInMills;
	}

	public static int returnMins(String deathTime) {
		try {
			int totalMins = compareTime(deathTime);
			return totalMins;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String timeString(long milliseconds) {
		String timeStr = "";
		int totalMins = (int) (milliseconds/60000);
		int totalHours = totalMins/60;
		int remainingMins = totalMins % 60;

		if(totalHours > 0) {
			String hourStr = "";
			String minStr = "";
			if(totalHours == 1) {
				hourStr = "1 hour";
			} else {
				hourStr = Integer.toString(totalHours) + " hours";
			}
			timeStr = hourStr;
			if(remainingMins > 0) {
				if(remainingMins == 1) {
					minStr = "1 minute";
				} else {
					minStr = Integer.toString(remainingMins) + " minutes";
				}
				timeStr = timeStr + " and " + minStr;
			}
		} else {
			if(totalMins < 1) {
				timeStr = "Less than a minute";
			}
			if(totalMins == 1) {
				timeStr = "1 minute";
			}
			if(totalMins > 1) {
				timeStr = Integer.toString(totalMins) + " minutes";
			}
		}
		return timeStr;
	}

	public static String dateFromMills(Long milliseconds) {
		Date dateFromMills = new Date(milliseconds);
		DateFormat df = new SimpleDateFormat("MMM d, yyyy");
		String date = df.format(dateFromMills);
		return date;
	}
}
