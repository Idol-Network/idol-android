package com.yiju.ldol.internal;

import android.content.Intent;

/**
 * Created by neavo on 2014/8/6.
 */

public class ExitIntent extends Intent {

	public final static String ACTION_EXIT_APPLICATION = "ACTION_EXIT_APPLICATION";

	public ExitIntent() {
		super(ACTION_EXIT_APPLICATION);
	}
}
