package com.wit.uilibrary.timer.widget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;

import com.wit.uilibrary.timer.util.TimerEditTextUtils;

public class TimerEditText extends EditText {
	public static class CountDownTimer extends android.os.CountDownTimer {
		private final TimerEditText timeRemainingTimerEditText;

		public CountDownTimer( final long millisInFuture,
				final long countDownInterval,
				final TimerEditText timeRemainingTimerEditText ) {
			super( millisInFuture, countDownInterval );

			this.timeRemainingTimerEditText = timeRemainingTimerEditText;
		}

		@Override
		public void onFinish() {
			this.timeRemainingTimerEditText.reset();

			final List<OnCountDownFinishedListener> onCountDownFinishedListeners =
					this.timeRemainingTimerEditText.getOnCountDownFinishedListeners();

			for ( final OnCountDownFinishedListener onCountDownFinishedListener : onCountDownFinishedListeners ) {
				onCountDownFinishedListener.onFinished();
			}
		}

		@Override
		public void onTick( final long millisUntilFinished ) {
			this.timeRemainingTimerEditText.setRemainingTime( millisUntilFinished );
			this.timeRemainingTimerEditText.clearFocus();
		}
	}

	public interface OnCountDownFinishedListener {
		public void onFinished();
	}

	public interface OnStatusChangedListener {
		public void onPaused();

		public void onStarted();
	}

	private class TimeRemainingOnClickListener implements View.OnClickListener {
		private final EditText timeRemainingEditText;

		public TimeRemainingOnClickListener(
				final EditText timeRemainingEditText ) {
			this.timeRemainingEditText = timeRemainingEditText;
		}

		@Override
		public void onClick( final View view ) {
			final Editable timeRemaining = this.timeRemainingEditText.getText();

			this.timeRemainingEditText.setSelection( timeRemaining.length() );
		}
	}

	private class TimeRemainingOnFocusChangeListener implements
			View.OnFocusChangeListener {
		private final EditText timeRemainingEditText;

		public TimeRemainingOnFocusChangeListener(
				final EditText timeRemainingEditText ) {
			this.timeRemainingEditText = timeRemainingEditText;
		}

		@Override
		public void onFocusChange( final View view, final boolean hasFocus ) {
			if ( hasFocus ) {
				final Editable timeRemaining =
						this.timeRemainingEditText.getText();

				this.timeRemainingEditText.setSelection( timeRemaining.length() );
			}
		}
	}

	private class TimeRemainingTextWatcher implements TextWatcher {
		private final TimerEditText timeRemainingTimerEditText;
		private boolean modifying = false;

		public TimeRemainingTextWatcher(
				final TimerEditText timeRemainingTimerEditText ) {
			this.timeRemainingTimerEditText = timeRemainingTimerEditText;
		}

		@Override
		public void afterTextChanged( final Editable editable ) {
			final String originalTimeRemainingString = editable.toString();
			String oldTimeRemainingString =
					new String( originalTimeRemainingString );

			if ( !this.modifying ) {
				this.modifying = true;
				oldTimeRemainingString =
						oldTimeRemainingString.replace( ":", "" );

				while ( ( oldTimeRemainingString.length() > 0 )
						&& ( oldTimeRemainingString.charAt( 0 ) == '0' ) ) {
					oldTimeRemainingString =
							oldTimeRemainingString.replaceFirst( "0", "" );
				}

				if ( oldTimeRemainingString.length() == 0 ) {
					this.timeRemainingTimerEditText.reset( editable );
				} else {
					final String newTimeRemainingString;

					if ( oldTimeRemainingString.length() == 1 ) {
						newTimeRemainingString =
								"00:0" + oldTimeRemainingString;
					} else if ( oldTimeRemainingString.length() == 2 ) {
						newTimeRemainingString = "00:" + oldTimeRemainingString;
					} else if ( oldTimeRemainingString.length() == 3 ) {
						newTimeRemainingString =
								"0" + oldTimeRemainingString.charAt( 0 ) + ":"
										+ oldTimeRemainingString.substring( 1 );
					} else if ( oldTimeRemainingString.length() == 4 ) {
						newTimeRemainingString =
								oldTimeRemainingString.substring( 0, 2 ) + ":"
										+ oldTimeRemainingString.substring( 2 );
					} else if ( oldTimeRemainingString.length() == 5 ) {
						newTimeRemainingString =
								oldTimeRemainingString.charAt( 0 )
										+ ":"
										+ oldTimeRemainingString.substring( 1,
												3 ) + ":"
										+ oldTimeRemainingString.substring( 3 );
					} else {
						newTimeRemainingString =
								oldTimeRemainingString.substring( 0, 2 )
										+ ":"
										+ oldTimeRemainingString.substring( 2,
												4 ) + ":"
										+ oldTimeRemainingString.substring( 4 );
					}

					editable.replace( 0, originalTimeRemainingString.length(),
							newTimeRemainingString );

					this.timeRemainingTimerEditText.setAnimation( null );

					final boolean running =
							this.timeRemainingTimerEditText.isRunning();

					if ( !running ) {
						this.timeRemainingTimerEditText.setModifiedSinceLastStart( true );
					}
				}

				this.modifying = false;
			}
		}

		@Override
		public void beforeTextChanged( final CharSequence charSequence,
				final int start, final int count, final int after ) {
		}

		@Override
		public void onTextChanged( final CharSequence charSequence,
				final int start, final int before, final int count ) {
		}
	}

	public static final String DEFAULT_TIME_REMAINING_STRING = "00:00";
	private static final AlphaAnimation TIMER_BLINKING_ANIMATION =
			new AlphaAnimation( 1.0f, 0.0f );
	private static final AlphaAnimation TIMER_FADE_OUT_ANIMATION =
			new AlphaAnimation( 1.0f, 0.0f );
	private static final AlphaAnimation TIMER_HIDE_ANIMATION =
			new AlphaAnimation( 1.0f, 0.0f );

	static {
		TimerEditText.TIMER_BLINKING_ANIMATION.setDuration( 100 );
		TimerEditText.TIMER_BLINKING_ANIMATION.setStartOffset( 500 );
		TimerEditText.TIMER_BLINKING_ANIMATION.setRepeatCount( Animation.INFINITE );
		TimerEditText.TIMER_BLINKING_ANIMATION.setRepeatMode( Animation.REVERSE );

		TimerEditText.TIMER_FADE_OUT_ANIMATION.setDuration( 250 );
		TimerEditText.TIMER_FADE_OUT_ANIMATION.setFillAfter( true );

		TimerEditText.TIMER_HIDE_ANIMATION.setFillAfter( true );
	}

	/**
	 * All the callbacks that listen to when this {@link TimerEditText} counts
	 * down to zero.
	 */
	private final List<OnCountDownFinishedListener> onCountDownFinishedListeners =
			new ArrayList<OnCountDownFinishedListener>();

	/**
	 * All the callbacks that listen to when this {@link TimerEditText} is
	 * started and paused.
	 */
	private final List<OnStatusChangedListener> onStatusChangedListeners =
			new ArrayList<OnStatusChangedListener>();

	/**
	 * The current count-down timer, if any, that is decrementing the remaining
	 * time.
	 */
	private TimerEditText.CountDownTimer countDownTimer;

	/**
	 * Whether the shown text has been modified by the user since the timer was
	 * created or last started.
	 */
	private boolean modifiedSinceLastStart = false;

	/**
	 * The amount of time shown.
	 */
	private long remainingTime = 0;

	public TimerEditText( final Context context, final AttributeSet attrs ) {
		super( context, attrs );

		this.setCursorVisible( false );
		this.setInputType( InputType.TYPE_CLASS_DATETIME
				| InputType.TYPE_DATETIME_VARIATION_TIME );

		final InputFilter[] inputFilters = new InputFilter[ 1 ];
		final InputFilter.LengthFilter lengthFilter =
				new InputFilter.LengthFilter( 8 );

		inputFilters[ 0 ] = lengthFilter;

		this.setFilters( inputFilters );

		this.setSelectAllOnFocus( true );

		final TimeRemainingOnClickListener timeRemainingOnClickListener =
				new TimeRemainingOnClickListener( this );
		final TimeRemainingTextWatcher timeRemainingTextWatcher =
				new TimeRemainingTextWatcher( this );
		final TimeRemainingOnFocusChangeListener timeRemainingOnFocusChangeListener =
				new TimeRemainingOnFocusChangeListener( this );

		this.addTextChangedListener( timeRemainingTextWatcher );
		this.setOnClickListener( timeRemainingOnClickListener );
		this.setOnEditorActionListener( null );
		this.setOnFocusChangeListener( timeRemainingOnFocusChangeListener );
		this.setTypeface();

		this.reset();
	}

	public void addOnCountDownFinishedListener(
			final OnCountDownFinishedListener onCountDownFinishedListener ) {
		this.onCountDownFinishedListeners.add( onCountDownFinishedListener );
	}

	public void addOnStatusChangedListener(
			final OnStatusChangedListener onStatusChangedListener ) {
		this.onStatusChangedListeners.add( onStatusChangedListener );
	}

	public void blink() {
		this.startAnimation( TimerEditText.TIMER_BLINKING_ANIMATION );
	}

	/**
	 * Stops the count-down timer that is currently running, if any.
	 */
	public void cancel() {
		if ( this.countDownTimer != null ) {
			this.countDownTimer.cancel();
			this.countDownTimer = null;
		}
	}

	public void fadeOut() {
		this.startAnimation( TimerEditText.TIMER_FADE_OUT_ANIMATION );
	}

	/**
	 * Returns the time in the future at which the timer should hit zero.
	 *
	 * @return The time in the future at which the timer should hit zero.
	 */
	public long getEndTime() {
		final long now = Calendar.getInstance().getTimeInMillis();
		final long endTime = this.remainingTime + now;

		return endTime;
	}

	public List<OnCountDownFinishedListener> getOnCountDownFinishedListeners() {
		return this.onCountDownFinishedListeners;
	}

	/**
	 * Returns the amount of time remaining in the timer.
	 *
	 * @return The amount of time remaining in the timer.
	 */
	public long getRemainingTime() {
		this.recalculateRemainingTimeIfNeeded();

		return this.remainingTime;
	}

	public void hide() {
		this.startAnimation( TimerEditText.TIMER_HIDE_ANIMATION );
	}

	/**
	 * Returns whether a count-down timer is currently running.
	 *
	 * @return Whether a count-down timer is currently running.
	 */
	public boolean isRunning() {
		final boolean running = this.countDownTimer != null;

		return running;
	}

	/**
	 * Pauses the count-down for this {@link TimerEditText}.
	 */
	public void pause() {
		this.cancel();

		for ( final OnStatusChangedListener onStatusChangedListener : this.onStatusChangedListeners ) {
			onStatusChangedListener.onPaused();
		}
	}

	/**
	 * Updates the internally-stored "remaining time" if the user has manually
	 * changed it since it was last calculated.
	 */
	private void recalculateRemainingTimeIfNeeded() {
		if ( this.modifiedSinceLastStart ) {
			final String remainingTimeString = this.getText().toString();

			this.remainingTime =
					TimerEditTextUtils.calculateTime( remainingTimeString );
			this.modifiedSinceLastStart = false;
		}
	}

	public void removeAllOnCountDownFinishedListeners() {
		this.onCountDownFinishedListeners.clear();
	}

	public void removeOnCountDownFinishedListener(
			final OnCountDownFinishedListener onCountDownFinishedListener ) {
		this.onCountDownFinishedListeners.remove( onCountDownFinishedListener );
	}

	public void removeOnStatusChangedListener(
			final OnStatusChangedListener onStatusChangedListener ) {
		this.onStatusChangedListeners.remove( onStatusChangedListener );
	}

	public void reset() {
		this.remainingTime = 0;

		this.setText( TimerEditText.DEFAULT_TIME_REMAINING_STRING );
		this.setSelection( TimerEditText.DEFAULT_TIME_REMAINING_STRING.length() );

		if ( this.isEnabled() ) {
			this.blink();
		}
	}

	public void reset( final Editable editable ) {
		this.remainingTime = 0;

		editable.replace( 0, editable.length(),
				TimerEditText.DEFAULT_TIME_REMAINING_STRING );
		this.setSelection( TimerEditText.DEFAULT_TIME_REMAINING_STRING.length() );

		if ( this.isEnabled() ) {
			this.blink();
		}
	}

	@Override
	public void setEnabled( final boolean enabled ) {
		super.setEnabled( enabled );

		if ( enabled
				&& this.getText().toString().equals(
						TimerEditText.DEFAULT_TIME_REMAINING_STRING ) ) {
			this.blink();
		}
	}

	/**
	 * Calculates and updates the amount of time that is shown based on the
	 * given end time.
	 *
	 * @param endTime
	 *            The time in the future at which the timer should hit zero.
	 */
	public void setEndTime( final long endTime ) {
		final long now = Calendar.getInstance().getTimeInMillis();
		final long remainingTime = endTime - now;

		this.setRemainingTime( remainingTime );
	}

	/**
	 * Sets whether the shown text has been modified by the user since the timer
	 * was created or last started.
	 *
	 * @param modifiedSinceLastRun
	 *            Whether the shown text has been modified by the user since the
	 *            timer was created or last started.
	 */
	public void setModifiedSinceLastStart( final boolean modifiedSinceLastStart ) {
		this.modifiedSinceLastStart = modifiedSinceLastStart;
	}

	/**
	 * Updates the amount of time shown.
	 *
	 * @param remainingTime
	 *            The new amount of time to show.
	 */
	public void setRemainingTime( final long remainingTime ) {
		this.remainingTime = remainingTime;

		final String remainingTimeString =
				TimerEditTextUtils.createTimeString( remainingTime );

		this.setText( remainingTimeString );

		this.modifiedSinceLastStart = false;
	}

	private void setTypeface() {
		final Context context = this.getContext();
		final AssetManager assetManager = context.getAssets();
		final Typeface typeface =
				Typeface.createFromAsset( assetManager, "font/Inconsolata.otf" );

		this.setTypeface( typeface );
	}

	/**
	 * Starts the count-down for this {@link TimerEditText}.
	 */
	public void start() {
		this.recalculateRemainingTimeIfNeeded();

		if ( this.remainingTime > 0 ) {
			this.countDownTimer =
					new TimerEditText.CountDownTimer( this.remainingTime, 100,
							this );

			this.countDownTimer.start();

			for ( final OnStatusChangedListener onStatusChangedListener : this.onStatusChangedListeners ) {
				onStatusChangedListener.onStarted();
			}
		} else {
			for ( final TimerEditText.OnCountDownFinishedListener onCountDownFinishedListener : this.onCountDownFinishedListeners ) {
				onCountDownFinishedListener.onFinished();
			}
		}
	}
}