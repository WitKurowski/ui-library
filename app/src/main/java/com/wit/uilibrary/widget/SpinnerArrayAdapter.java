package com.wit.uilibrary.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public abstract class SpinnerArrayAdapter<T> extends ArrayAdapter<T> {
	private final List<T> objects;
	private final int itemViewResourceId;
	private final int dropDownItemViewResourceId;
	private final boolean hasDefaultOption;
	private final String defaultOptionString;

	public SpinnerArrayAdapter( final Context context, final List<T> objects ) {
		this( context, objects, android.R.layout.simple_spinner_item,
				android.R.layout.simple_spinner_dropdown_item, null );
	}

	public SpinnerArrayAdapter( final Context context, final List<T> objects,
			final int itemViewResourceId, final int dropDownItemViewResourceId ) {
		this( context, objects, itemViewResourceId, dropDownItemViewResourceId,
				null );
	}

	public SpinnerArrayAdapter( final Context context, final List<T> objects,
			final int itemViewResourceId, final int dropDownItemViewResourceId,
			final String defaultOptionString ) {
		super( context, -1, objects );

		this.objects = objects;
		this.itemViewResourceId = itemViewResourceId;
		this.dropDownItemViewResourceId = dropDownItemViewResourceId;
		this.hasDefaultOption = defaultOptionString != null;
		this.defaultOptionString = defaultOptionString;
	}

	public SpinnerArrayAdapter( final Context context, final List<T> objects,
			final String defaultOptionString ) {
		this( context, objects, android.R.layout.simple_spinner_item,
				android.R.layout.simple_spinner_dropdown_item,
				defaultOptionString );
	}

	@Override
	public int getCount() {
		int count = this.objects.size();

		if ( this.hasDefaultOption ) {
			++count;
		}

		return count;
	}

	@Override
	public View getDropDownView( final int position, final View convertView,
			final ViewGroup parent ) {
		final View rootView;

		if ( convertView == null ) {
			final Context context = parent.getContext();
			final LayoutInflater layoutInflater = LayoutInflater.from( context );

			rootView =
					layoutInflater.inflate( this.dropDownItemViewResourceId,
							parent, false );
		} else {
			rootView = convertView;
		}

		this.setText( rootView, position );

		return rootView;
	}

	@Override
	public T getItem( final int position ) {
		final int adjustedPosition;

		if ( this.hasDefaultOption ) {
			adjustedPosition = position - 1;
		} else {
			adjustedPosition = position;
		}

		final T item;

		if ( adjustedPosition >= 0 ) {
			item = this.objects.get( adjustedPosition );
		} else {
			item = null;
		}

		return item;
	}

	protected abstract String getText( final T object );

	@Override
	public View getView( final int position, final View convertView,
			final ViewGroup parent ) {
		final View rootView;

		if ( convertView == null ) {
			final Context context = parent.getContext();
			final LayoutInflater layoutInflater = LayoutInflater.from( context );

			rootView =
					layoutInflater.inflate( this.itemViewResourceId, parent,
							false );
		} else {
			rootView = convertView;
		}

		this.setText( rootView, position );

		return rootView;
	}

	@Override
	public boolean isEmpty() {
		return this.objects.isEmpty() && !this.hasDefaultOption;
	}

	private void setText( final View rootView, final int position ) {
		final TextView textView =
				(TextView) rootView.findViewById( android.R.id.text1 );
		final String text;

		if ( ( position == 0 ) && this.hasDefaultOption ) {
			text = this.defaultOptionString;
		} else {
			final T object = this.getItem( position );
			text = this.getText( object );
		}

		textView.setText( text );
	}
}