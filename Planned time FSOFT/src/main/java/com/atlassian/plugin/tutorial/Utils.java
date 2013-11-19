package com.atlassian.plugin.tutorial;

/**
 * Created by IntelliJ IDEA.
 * User: thaivm
 * Date: 6/21/13
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */


import org.radeox.util.logging.SystemOutLogger;

import java.sql.Timestamp;
import java.util.Calendar;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class Utils {
	public static final String SAT = "Sat";
	public static final String SUN = "Sun";

	/**
	 *Key: "SAT" for Saturday and "SUN" for Sunday
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public static Map<String,Integer>  getSumSSday(Timestamp fromDate, Timestamp toDate){
		Map<String,Integer> result = new HashMap<String, Integer>();
		result.put(Utils.SAT, 0);
		result.put(Utils.SUN, 0);
		Calendar from = new GregorianCalendar();
		from.setTimeInMillis(fromDate.getTime());
		Calendar to = new GregorianCalendar();
		to.setTimeInMillis(toDate.getTime());
		//check to after from, not check toDate.
		while(to.after(from)){
			if(from.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY) result.put(Utils.SAT, result.get(Utils.SAT)+1);
			if(from.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) result.put(Utils.SUN, result.get(Utils.SUN)+1);
			from.add(Calendar.DATE,1);
		}
		//check toDate
		if(to.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY) result.put(Utils.SAT, result.get(Utils.SAT)+1);
		if(to.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) result.put(Utils.SUN, result.get(Utils.SUN)+1);
		return result;
	}
	/**
	 *
	 * @param date
	 * @return
	 */
	public static Timestamp getLastDateInMonth(Timestamp date){
		Calendar today = new GregorianCalendar();
		today.setTimeInMillis(date.getTime());
        int lastDate = today.getActualMaximum(Calendar.DAY_OF_MONTH);

        String month = String.valueOf(today.get(Calendar.MONTH)).length()==1 ?
                "0"+(today.get(Calendar.MONTH) + 1) : String.valueOf(today.get(Calendar.MONTH) + 1);
        Timestamp ts = Timestamp.valueOf(today.get(Calendar.YEAR)+"-"+ month  +"-"+lastDate+" 00:00:00");
		return ts;
	}

    public static long getPlanDay(Timestamp fromDate, Timestamp toDate){
        Map<String,Integer> result =  getSumSSday(fromDate,toDate);
        int sumSat = result.get(Utils.SAT);
        int sumSun = result.get(Utils.SUN);
        long distance = (toDate.getTime() - fromDate.getTime())/(1000*60*60*24) - sumSat - sumSun + 1;
        return distance;
    }

    public static void main(String[] args){
        System.out.println(Utils.getLastDateInMonth(Timestamp.valueOf("2013-05-01 00:00:00")));
    }
}
