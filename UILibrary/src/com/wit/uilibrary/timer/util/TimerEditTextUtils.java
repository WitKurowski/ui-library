package com.wit.uilibrary.timer.util;

public class TimerEditTextUtils {
	public static long calculateTime( final String timeString ) {
		final String[] splitTimeStrings = timeString.split( ":" );
		final long seconds;
		final long minutes;
		final long hours;

		if ( splitTimeStrings.length == 2 ) {
			seconds = Long.parseLong( splitTimeStrings[ 1 ] );
			minutes = Long.parseLong( splitTimeStrings[ 0 ] );
			hours = 0;
		} else {
			seconds = Long.parseLong( splitTimeStrings[ 2 ] );
			minutes = Long.parseLong( splitTimeStrings[ 1 ] );
			hours = Long.parseLong( splitTimeStrings[ 0 ] );
		}

		final long time =
				( hours * 60 * 60 * 1000 ) + ( minutes * 60 * 1000 )
						+ ( seconds * 1000 );

		return time;
	}

	public static String createTimeString( final long time ) {
		final long seconds = (long) Math.ceil( time / 1000d );
		final long minutes = (long) Math.ceil( seconds / 60 );
		final long hours = (long) Math.ceil( minutes / 60 );
		final long secondsToDisplay = seconds % 60;
		final long minutesToDisplay = minutes % 60;
		final long hoursToDisplay = hours;
		final StringBuffer timeRemaining = new StringBuffer();

		if ( hoursToDisplay > 0 ) {
			timeRemaining.append( hoursToDisplay + ":" );
		}

		if ( minutesToDisplay <= 9 ) {
			timeRemaining.append( "0" );
		}

		timeRemaining.append( minutesToDisplay );

		if ( secondsToDisplay <= 9 ) {
			timeRemaining.append( "0" );
		}

		timeRemaining.append( secondsToDisplay );

		return timeRemaining.toString();
	}
}