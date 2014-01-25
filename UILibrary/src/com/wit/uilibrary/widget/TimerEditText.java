package com.wit.uilibrary.widget;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;

public class TimerEditText extends EditText {
	public static class TimerEditTextCountDownTimer extends CountDownTimer {
		private final TimerEditText timeRemainingTimerEditText;

		public TimerEditTextCountDownTimer( final long millisInFuture,
				final long countDownInterval,
				final TimerEditText timeRemainingTimerEditText ) {
			super( millisInFuture, countDownInterval );

			this.timeRemainingTimerEditText = timeRemainingTimerEditText;
		}

		@Override
		public void onFinish() {
			this.timeRemainingTimerEditText.reset();
		}

		@Override
		public void onTick( final long millisUntilFinished ) {
			final long secondsUntilFinished =
					(long) Math.ceil( millisUntilFinished / 1000d );
			final long minutesUntilFinished =
					(long) Math.ceil( secondsUntilFinished / 60 );
			final long hoursUntilFinished =
					(long) Math.ceil( minutesUntilFinished / 60 );
			final long secondsToDisplay = secondsUntilFinished % 60;
			final long minutesToDisplay = minutesUntilFinished % 60;
			final long hoursToDisplay = hoursUntilFinished;
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

			this.timeRemainingTimerEditText.setText( timeRemaining );
		}
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
					this.timeRemainingTimerEditText.reset();
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

	private static final String DEFAULT_TIME_REMAINING_STRING = "00:00";
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

	public void blink() {
		this.startAnimation( TimerEditText.TIMER_BLINKING_ANIMATION );
	}

	public void fadeOut() {
		this.startAnimation( TimerEditText.TIMER_FADE_OUT_ANIMATION );
	}

	public void hide() {
		this.startAnimation( TimerEditText.TIMER_HIDE_ANIMATION );
	}

	public void reset() {
		this.setText( TimerEditText.DEFAULT_TIME_REMAINING_STRING );
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

	private void setTypeface() {
		final Context context = this.getContext();
		final AssetManager assetManager = context.getAssets();
		final Typeface typeface =
				Typeface.createFromAsset( assetManager, "font/Inconsolata.otf" );

		this.setTypeface( typeface );
	}
}
