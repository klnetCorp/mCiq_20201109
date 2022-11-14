package mciq2.klnet.co.kr;

import android.content.Context;
import android.os.Build;

import java.util.UUID;

public class DataSet {
	public String isrunning = "false";
	public String islogin = "false";
	public String userid = "";
	public String isbackground = "false";
	public String istext = "false";
	public boolean isrunapppush = false;

	public String recv_id = "";

	public String push_id;	// 푸시ID
	public String msg;    		// 알림 메세지
	public String obj_id;		// 푸시 연관 계시물 ID
	public String type;		// 메세지 종류



	public static String push_url = "https://testpush.plism.com";
	//public static String push_url = "https://push.plism.com";
	public static String connect_url = "https://devmciq.plism.com";
	//public static String connect_url = "https://mciq.plism.com";


	private static DataSet _instance;

	static {
		_instance = new DataSet();
	}

	private DataSet() {
		
	}

	public static DataSet getInstance() {
		return _instance;
	}

	public static String getDeviceID(Context context)
	{
		String serial = "";
		String androidId = "";
		try {
			serial = (String) Build.class.getField("SERIAL").get(null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(), serial.hashCode());
		return deviceUuid.toString();
	}

}
