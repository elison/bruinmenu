package com.st40611.bruinmenu.sensor;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

public class WeakRefHandler extends Handler {
	// Handlers that are inner classes of activities should be static
	// with weak references to avoid outer class from being leaked

	private WeakReference<HasHandler> f = null;
	
	public WeakRefHandler(HasHandler f) {
		this.f = new WeakReference<HasHandler>(f);
	}
	
	@Override
	public void handleMessage(Message msg) {
		HasHandler fwh = f.get();
		if (fwh != null)
			fwh.processUpdate(msg);
	}
}
