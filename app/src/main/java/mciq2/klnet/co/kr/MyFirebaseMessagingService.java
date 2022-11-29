package mciq2.klnet.co.kr;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //앱 실행 아이콘 개수 조절
        setBadge(1);
        //추가한것
        Log.i("CHECK", "onMessageReceived"+remoteMessage.getData().toString());
        String title = "";
        String message = "";
        if( remoteMessage.getNotification() == null   ){
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("body");
        }
        else{
            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();
        }

        String msg = remoteMessage.getData().get("msg");
        JSONObject data = null;
        Log.d("###","msg : " + msg);
        try {
            data = new JSONObject(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String add = remoteMessage.getData().get("add");
        String alert = remoteMessage.getData().get("alert");
        DataSet.getInstance().recv_id=remoteMessage.getData().get("userid");

        Log.i("CHECK", "recv_id : "+DataSet.getInstance().recv_id);
        sendNotification(title, message, data, add, alert);
    }
    // [END receive_message]

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    public void handleIntent(Intent intent) {
        Log.i( "CHECK", "handleIntent~~~~ ");
    }


    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * messageBody FCM message body received.
     */
    private void sendNotification(String title, String body, JSONObject data, String add, String alert) {
        String seq = null;
        String type = null;
        String doc_gubun = null;
        String param = null;
        if (data != null) {
            try {
                seq = data.getString("seq");
                type = data.getString("type");
                doc_gubun = data.getString("doc_gubun");
                param = data.getString("param");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.i("CHECK", "seq:" + seq);
        Log.i("CHECK", "type:" + type);
        Log.i("CHECK", "doc_gubun:" + doc_gubun);
        Log.i("CHECK", "param:" + param);
        Log.i("CHECK", "add:" + add);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("push_id", seq);
        intent.putExtra("msg", alert);
        intent.putExtra("recv_id", "");
        intent.putExtra("type", doc_gubun);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.

            String channelName = getString(R.string.default_notification_channel_name);
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder = new NotificationCompat.Builder(this, channel.getId());

        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
        }

        notificationBuilder.setSmallIcon(R.drawable.small_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setNumber(1)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);



       // notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        Intent intent3 = new Intent(this, MainActivity.class);
        intent3.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent3.putExtra("push_id", seq);
        intent3.putExtra("msg", alert);
        //intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent3);
    }

    private String getLauncherClassName() {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        final PackageManager pm = getApplicationContext().getPackageManager();
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resolveInfos) {
            final String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }


    public void setBadge(int count) {
        String launcherClassName = getLauncherClassName();
        if (launcherClassName == null) {
            return;
        }

        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count > 0? count : null);
        intent.putExtra("badge_count_package_name", getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);

        sendBroadcast(intent);
    }

}
