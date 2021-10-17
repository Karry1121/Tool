package com.karry.tool.Bean;

public class SettingsBean {
	public static final String API_KEY_HISTORY = "f08f3c8855703d40f7d8e2f4513b8b22";
	public static final String API_KEY_QUERY_PHONE_LOCATION = "b8b6833f6eeb0ea0cd29de5ddfa0a4b8";
	public static final String API_KEY_QRCODE = "ec74e287c681302561dd95f5d8daff9b";
	public static final String API_OIL_PRICE = "aae8dbbf81d8488b8255cd82fea657ff";
	public static boolean isPoetryAutoOpenBrowser = true;
	public static boolean isOpenScanHistory = true;
	public static int QRCodeSize = 300;
	public static String SP_NAME_SETTINGS = "settings_data";
	public static String SP_NAME_HISTORY_TODAY = "history_data";
	public static String SP_NAME_SCAN_HISTORY = "scan_history";
	public static String SP_NAME_OIL_PRICE = "oil_price";

	public static void loadDefault() {
		isPoetryAutoOpenBrowser = true;
		isOpenScanHistory = true;
		QRCodeSize = 300;
	}
}
