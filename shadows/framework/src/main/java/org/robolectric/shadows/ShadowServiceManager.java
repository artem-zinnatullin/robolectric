package org.robolectric.shadows;

import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.M;
import static android.os.Build.VERSION_CODES.N_MR1;
import static android.os.Build.VERSION_CODES.O;
import static android.os.Build.VERSION_CODES.P;
import static android.os.Build.VERSION_CODES.Q;

import android.accounts.IAccountManager;
import android.app.IAlarmManager;
import android.app.INotificationManager;
import android.app.ISearchManager;
import android.app.IUiModeManager;
import android.app.IWallpaperManager;
import android.app.admin.IDevicePolicyManager;
import android.app.job.IJobScheduler;
import android.app.role.IRoleManager;
import android.app.slice.ISliceManager;
import android.app.trust.ITrustManager;
import android.app.usage.IUsageStatsManager;
import android.content.Context;
import android.content.IClipboard;
import android.content.IRestrictionsManager;
import android.content.pm.ICrossProfileApps;
import android.content.pm.IShortcutService;
import android.content.rollback.IRollbackManager;
import android.hardware.biometrics.IBiometricService;
import android.hardware.fingerprint.IFingerprintService;
import android.hardware.input.IInputManager;
import android.hardware.location.IContextHubService;
import android.hardware.usb.IUsbManager;
import android.location.ICountryDetector;
import android.location.ILocationManager;
import android.media.IAudioService;
import android.media.IMediaRouterService;
import android.media.session.ISessionManager;
import android.net.IConnectivityManager;
import android.net.INetworkPolicyManager;
import android.net.INetworkScoreService;
import android.net.nsd.INsdManager;
import android.net.wifi.IWifiManager;
import android.net.wifi.p2p.IWifiP2pManager;
import android.net.wifi.rtt.IWifiRttManager;
import android.os.BatteryStats;
import android.os.Binder;
import android.os.IBatteryPropertiesRegistrar;
import android.os.IBinder;
import android.os.IInterface;
import android.os.IPowerManager;
import android.os.IThermalService;
import android.os.IUserManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.storage.IStorageManager;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IBatteryStats;
import com.android.internal.appwidget.IAppWidgetService;
import com.android.internal.os.IDropBoxManagerService;
import com.android.internal.view.IInputMethodManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.Resetter;
import org.robolectric.util.ReflectionHelpers;

/** Shadow for {@link ServiceManager}. */
@SuppressWarnings("NewApi")
@Implements(value = ServiceManager.class, isInAndroidSdk = false)
public class ShadowServiceManager {

  private static final Map<String, ServiceDescriptor> serviceDescriptors = new HashMap<>();
  private static final Set<String> unavailableServices = new HashSet<>();
  private static final Map<String, IBinder> cachedServices = new HashMap<>();

  static {
    addServiceDescriptor(Context.CLIPBOARD_SERVICE, IClipboard.class);
    addServiceDescriptor(Context.WIFI_P2P_SERVICE, IWifiP2pManager.class);
    addServiceDescriptor(Context.ACCOUNT_SERVICE, IAccountManager.class);
    addServiceDescriptor(Context.USB_SERVICE, IUsbManager.class);
    addServiceDescriptor(Context.LOCATION_SERVICE, ILocationManager.class);
    addServiceDescriptor(Context.INPUT_METHOD_SERVICE, IInputMethodManager.class);
    addServiceDescriptor(Context.ALARM_SERVICE, IAlarmManager.class);
    addServiceDescriptor(Context.POWER_SERVICE, IPowerManager.class);
    addServiceDescriptor(BatteryStats.SERVICE_NAME, IBatteryStats.class);
    addServiceDescriptor(Context.DROPBOX_SERVICE, IDropBoxManagerService.class);
    addServiceDescriptor(Context.DEVICE_POLICY_SERVICE, IDevicePolicyManager.class);
    addServiceDescriptor(Context.CONNECTIVITY_SERVICE, IConnectivityManager.class);
    addServiceDescriptor(Context.WIFI_SERVICE, IWifiManager.class);
    addServiceDescriptor(Context.SEARCH_SERVICE, ISearchManager.class);
    addServiceDescriptor(Context.UI_MODE_SERVICE, IUiModeManager.class);
    addServiceDescriptor(Context.NETWORK_POLICY_SERVICE, INetworkPolicyManager.class);
    addServiceDescriptor(Context.INPUT_SERVICE, IInputManager.class);
    addServiceDescriptor(Context.COUNTRY_DETECTOR, ICountryDetector.class);
    addServiceDescriptor(Context.NSD_SERVICE, INsdManager.class);
    addServiceDescriptor(Context.AUDIO_SERVICE, IAudioService.class);
    addServiceDescriptor(Context.APPWIDGET_SERVICE, IAppWidgetService.class);
    addServiceDescriptor(Context.NOTIFICATION_SERVICE, INotificationManager.class);
    addServiceDescriptor(Context.WALLPAPER_SERVICE, IWallpaperManager.class);

    if (RuntimeEnvironment.getApiLevel() >= JELLY_BEAN_MR1) {
      addServiceDescriptor(Context.USER_SERVICE, IUserManager.class);
    }
    if (RuntimeEnvironment.getApiLevel() >= JELLY_BEAN_MR2) {
      addServiceDescriptor(Context.APP_OPS_SERVICE, IAppOpsService.class);
    }
    if (RuntimeEnvironment.getApiLevel() >= KITKAT) {
      addServiceDescriptor("batteryproperties", IBatteryPropertiesRegistrar.class);
    }
    if (RuntimeEnvironment.getApiLevel() >= LOLLIPOP) {
      addServiceDescriptor(Context.RESTRICTIONS_SERVICE, IRestrictionsManager.class);
      addServiceDescriptor(Context.TRUST_SERVICE, ITrustManager.class);
      addServiceDescriptor(Context.JOB_SCHEDULER_SERVICE, IJobScheduler.class);
      addServiceDescriptor(Context.NETWORK_SCORE_SERVICE, INetworkScoreService.class);
      addServiceDescriptor(Context.USAGE_STATS_SERVICE, IUsageStatsManager.class);
      addServiceDescriptor(Context.MEDIA_ROUTER_SERVICE, IMediaRouterService.class);
      addServiceDescriptor(Context.MEDIA_SESSION_SERVICE, ISessionManager.class, true);
    }
    if (RuntimeEnvironment.getApiLevel() >= M) {
      addServiceDescriptor(Context.FINGERPRINT_SERVICE, IFingerprintService.class);
    }
    if (RuntimeEnvironment.getApiLevel() >= N_MR1) {
      addServiceDescriptor(Context.SHORTCUT_SERVICE, IShortcutService.class);
    }
    if (RuntimeEnvironment.getApiLevel() >= O) {
      addServiceDescriptor("mount", IStorageManager.class);
    } else {
      addServiceDescriptor("mount", "android.os.storage.IMountService");
    }
    if (RuntimeEnvironment.getApiLevel() >= P) {
      addServiceDescriptor(Context.SLICE_SERVICE, ISliceManager.class);
      addServiceDescriptor(Context.CROSS_PROFILE_APPS_SERVICE, ICrossProfileApps.class);
      addServiceDescriptor(Context.WIFI_RTT_RANGING_SERVICE, IWifiRttManager.class);
      addServiceDescriptor(Context.CONTEXTHUB_SERVICE, IContextHubService.class);
    }
    if (RuntimeEnvironment.getApiLevel() >= Q) {
      addServiceDescriptor(Context.BIOMETRIC_SERVICE, IBiometricService.class);
      addServiceDescriptor(Context.ROLE_SERVICE, IRoleManager.class);
      addServiceDescriptor(Context.ROLLBACK_SERVICE, IRollbackManager.class);
      addServiceDescriptor(Context.THERMAL_SERVICE, IThermalService.class);
    }
  }

  private static class ServiceDescriptor {

    private Class<? extends IInterface> clazz;
    private String className;
    private boolean useDeepBinder;

    ServiceDescriptor(Class<? extends IInterface> clazz) {
      this.clazz = clazz;
    }

    ServiceDescriptor(Class<? extends IInterface> clazz, boolean useDeepBinder) {
      this.clazz = clazz;
      this.useDeepBinder = useDeepBinder;
    }

    ServiceDescriptor(String className) {
      this.className = className;
    }

    IBinder createBinder() {
      if (className == null) {
        className = clazz.getCanonicalName();
      } else if (clazz == null) {
        try {
          clazz = (Class<IInterface>) Class.forName(className);
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      }
      Binder binder = new Binder();
      binder.attachInterface(
          useDeepBinder
              ? ReflectionHelpers.createDeepProxy(clazz)
              : ReflectionHelpers.createNullProxy(clazz),
          className);
      return binder;
    }
  }

  protected static void addServiceDescriptor(String name, Class<? extends IInterface> clazz) {
    serviceDescriptors.put(name, new ServiceDescriptor(clazz));
  }

  protected static void addServiceDescriptor(String name, String serviceDescriptor) {
    serviceDescriptors.put(name, new ServiceDescriptor(serviceDescriptor));
  }

  protected static void addServiceDescriptor(
      String name, Class<? extends IInterface> clazz, boolean useDeepBinder) {
    serviceDescriptors.put(name, new ServiceDescriptor(clazz, true));
  }
  /**
   * Returns the binder associated with the given system service. If the given service is set to
   * unavailable in {@link #setServiceAvailability}, {@code null} will be returned.
   */
  @Implementation
  protected static IBinder getService(String name) {
    if (unavailableServices.contains(name)) {
      return null;
    }
    if (serviceDescriptors.containsKey(name) && !cachedServices.containsKey(name)) {
      ServiceDescriptor serviceDescriptor = serviceDescriptors.get(name);
      cachedServices.put(name, serviceDescriptor.createBinder());
    }
    return cachedServices.get(name);
  }

  @Implementation
  protected static void addService(String name, IBinder service) {}

  @Implementation
  protected static IBinder checkService(String name) {
    return null;
  }

  @Implementation
  protected static String[] listServices() throws RemoteException {
    return null;
  }

  @Implementation
  protected static void initServiceCache(Map<String, IBinder> cache) {}

  /**
   * Sets the availability of the given system service. If the service is set as unavailable,
   * subsequent calls to {@link Context#getSystemService} for that service will return {@code null}.
   */
  public static void setServiceAvailability(String service, boolean available) {
    if (available) {
      unavailableServices.remove(service);
    } else {
      unavailableServices.add(service);
    }
  }

  @Resetter
  public static void reset() {
    unavailableServices.clear();
  }
}
