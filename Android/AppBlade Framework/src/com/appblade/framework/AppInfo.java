package com.appblade.framework;


import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.appblade.framework.utils.StringUtils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;

/**
 * AppInfo
 * Class that stores AppBlade registration information as well as other useful app information, incluing PackageInfo, whether the package is signed, and whether the app is being run in the emulator.
 * @author andrewtremblay
 *
 */
public class AppInfo {
	static final String DefaultUDID = "0000000000000000000000000000000000000000";
	public static String DefaultAppBladeHost = "AppBlade.com";
	public static String DefaultServiceScheme = "https://";

	public String Token;
	public String Secret;
	public String AppId;
	public String Issuance;
	public String Ext = DefaultUDID;
	public String CurrentEndpoint = DefaultAppBladeHost;
	public String CurrentServiceScheme = DefaultServiceScheme;
	
	PackageInfo PackageInfo;
	private String systemInfo;
	
	/**
	 * Initializes systemInfo string if it does not yet exist.
	 * @return String of all current interesting device information, see initSystemInfo() for a breakdown.
	 */
	public synchronized String getSystemInfo()
	{
		if(StringUtils.isNullOrEmpty(systemInfo))
		{
			initSystemInfo();
		}
		return systemInfo;
	}

	/**
	 * Initializer for all interesting device information. Will include Package information if that is available.
	 */
	void initSystemInfo() {
		StringBuilder builder = new StringBuilder();

		StringUtils.append(builder, "BOARD:  %s%n", Build.BOARD);
		StringUtils.append(builder, "BRAND:  %s%n", Build.BRAND);
		StringUtils.append(builder, "DEVICE:  %s%n", Build.DEVICE);
		StringUtils.append(builder, "DISPLAY:  %s%n", Build.DISPLAY);
		StringUtils.append(builder, "FINGERPRINT:  %s%n", Build.FINGERPRINT);
		StringUtils.append(builder, "ID:  %s%n", Build.ID);
		StringUtils.append(builder, "MANUFACTURER:  %s%n", Build.MANUFACTURER);
		StringUtils.append(builder, "MODEL:  %s%n", Build.MODEL);
		StringUtils.append(builder, "RELEASE:  %s%n", Build.VERSION.RELEASE);
		StringUtils.append(builder, "SDK_INT:  %d%n", Build.VERSION.SDK_INT);
		
		StringUtils.append(builder, "%n");
		
		if(PackageInfo != null)
		{
			StringUtils.append(builder, "Package name:  %s%n", PackageInfo.packageName);
			StringUtils.append(builder, "Version name:  %s%n", PackageInfo.versionName);
			
			StringUtils.append(builder, "%n");
		}
		
		systemInfo = builder.toString();
	}
		
	//a good check to see if we are in eclipse, since that doesn't sign apks before uploading to attached devices
	/**
	 * Static call to check if we are signed (a.k.a. running in eclipse), checks for the existence of a certificate and only returns true on its existence. 
	 * This would be a good check to call if you didn't want errors, sessions, or feedback to be logged while you were just developing the app locally.
	 * @param pi the package info we are checking, usually just AppInfo.PackageInfo
	 * @return whether we are signed, which is the same as saying whether we are running the apk locally.
	 */
	public static boolean isSigned(PackageInfo pi) {
		boolean toRet = false;
		if(pi != null){
			ApplicationInfo ai = pi.applicationInfo;
			ZipFile zf;
				try {
					zf = new ZipFile(ai.sourceDir);
					ZipEntry ze = zf.getEntry("META-INF/CERT.DSA");
					toRet = (ze != null);
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return toRet;
	}
	
	/**
	 * Static call to check if we are running in the emulator (a.k.a. development), checks for the existence of a certificate
	 * @param pi the package info we are checking, usually just AppInfo.PackageInfo
	 * @return whether we are in an emulator
	 */
	public static boolean isInEmulator(PackageInfo pi) {
		//There is apparently a better way to do this by checking if the debug certificate is included, but right now checking that the cert is missing's our best bet 
		boolean toRet = false;
		if(pi != null){
			ApplicationInfo ai = pi.applicationInfo;
			ZipFile zf;
				try {
					zf = new ZipFile(ai.sourceDir);
					ZipEntry ze = zf.getEntry("META-INF/CERT.DSA");
					toRet = (ze != null);
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return toRet;
	}

	/**
	 * Checks validity (existence) of AppBlade variables. If we don't have that we prety much can't do anything. 
	 * @return
	 */
	public boolean isValid() {
		return
				!StringUtils.isNullOrEmpty(AppId) &&
				!StringUtils.isNullOrEmpty(Token) &&
				!StringUtils.isNullOrEmpty(Secret) &&
				!StringUtils.isNullOrEmpty(Issuance);
	}

}
