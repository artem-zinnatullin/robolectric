package org.robolectric;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConfigTestReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
  }

  static public class InnerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

    }
  }
}
