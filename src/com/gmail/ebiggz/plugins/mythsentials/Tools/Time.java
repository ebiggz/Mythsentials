package com.gmail.ebiggz.plugins.mythsentials.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.gmail.ebiggz.plugins.mythsentials.Mythsentials;

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
    
    private static int compareTime(String deathTime) throws ParseException {
    	
    	String dragonDeath = deathTime;
    	String currentTime = getTime();
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.ENGLISH);
    	Date date1 = sdf.parse(dragonDeath);
    	Date date2 = sdf.parse(currentTime);
    	
    	Long differnceInMills = date2.getTime() - date1.getTime();
    	
    	long timeInMinutes = differnceInMills/60000;
    	int totalMinutes = (int) timeInMinutes;
    	
		return totalMinutes;   	
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
}
