package com.yiju.ldol.internal;

import android.content.IntentFilter;

/**
 * Created by neavo on 2014/8/6.
 */

public class ExitIntentFilter extends IntentFilter {

	public final static String ACTION_EXIT_APPLICATION = "ACTION_EXIT_APPLICATION";

	public ExitIntentFilter() {
		super(ACTION_EXIT_APPLICATION);
	}
}
