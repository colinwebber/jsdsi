/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.util;

import java.util.Calendar;
import java.util.Date;


/**
 * Static utility class for java.util.Dates.
 * 
 * @author Sean Radford
 * @version $Revision: 1.2 $ $Date: 2004/05/07 13:17:39 $
 */
public class DateUtil {

    private DateUtil() {
        //
    }
    
	/**
	 * Returns a new <code>java.util.Date</code> set to 'now' except that the milliseconds are zero
	 *
	 * @return the date
	 */
	public static Date newDate() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * Returns a zero-millisecond <code>java.util.Date</code> set to 'now' but with the YEAR modified by yearChange
	 *
	 * @param yearChange The value to change the year by
	 * @return the date
	 */
	public static Date newDate(int yearChange) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.YEAR, c.get(Calendar.YEAR) + yearChange);
		return c.getTime();
	}

	/**
	 * Returns a zero-millisecond <code>java.util.Date</code> set to 'now' but with the YEAR modified by yearChange
	 * and the MONTH modified by monthChange
	 *
	 * @param yearChange
	 * @param monthChange
	 * @return the date
	 */
	public static Date newDate(int yearChange, int monthChange) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.YEAR, c.get(Calendar.YEAR) + yearChange);
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) + monthChange);
		return c.getTime();
	}

	/**
	 * Returns a zero-millisecond <code>java.util.Date</code> set to 'now' but with the YEAR modified by yearChange,
	 * the MONTH modified by monthChange, and the DAY modified by dayChange
	 * 
	 * @param yearChange
	 * @param monthChange
	 * @param dayChange
	 * @return the date
	 */
	public static Date newDate(int yearChange, int monthChange, int dayChange) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.YEAR, c.get(Calendar.YEAR) + yearChange);
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) + monthChange);
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + dayChange);
		return c.getTime();
	}
	
	/**
	 * @param yearChange
	 * @param monthChange
	 * @param dayChange
	 * @param hourChange
	 * @return the date
	 */
	public static Date newDate(int yearChange, int monthChange, int dayChange, int hourChange) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.YEAR, c.get(Calendar.YEAR) + yearChange);
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) + monthChange);
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + dayChange);
		c.set(Calendar.HOUR, c.get(Calendar.HOUR) + hourChange);
		return c.getTime();
	}

	/**
	 * @param yearChange
	 * @param monthChange
	 * @param dayChange
	 * @param hourChange
	 * @param minuteChange
	 * @return the date
	 */
	public static Date newDate(int yearChange, int monthChange, int dayChange, int hourChange, int minuteChange) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.YEAR, c.get(Calendar.YEAR) + yearChange);
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) + monthChange);
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + dayChange);
		c.set(Calendar.HOUR, c.get(Calendar.HOUR) + hourChange);
		c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + minuteChange);
		return c.getTime();
	}

	/**
	 * @param yearChange
	 * @param monthChange
	 * @param dayChange
	 * @param hourChange
	 * @param minuteChange
	 * @param secondChange
	 * @return the date
	 */
	public static Date newDate(int yearChange, int monthChange, int dayChange, int hourChange, int minuteChange, int secondChange) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.YEAR, c.get(Calendar.YEAR) + yearChange);
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) + monthChange);
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + dayChange);
		c.set(Calendar.HOUR, c.get(Calendar.HOUR) + hourChange);
		c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + minuteChange);
		c.set(Calendar.SECOND, c.get(Calendar.SECOND) + secondChange);
		return c.getTime();
	}
}
