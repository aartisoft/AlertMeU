package in.alertmeu.FirebaseNotification;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Random;

import in.alertmeu.R;
import in.alertmeu.utils.BadgeNumberManager;
import in.alertmeu.utils.BadgeNumberManagerVIVO;
import in.alertmeu.utils.BadgeUtils;


public class MyNotificationManager {

    public static final int ID_BIG_NOTIFICATION = 234;
    public static final int ID_SMALL_NOTIFICATION = 235;

    private Context mCtx;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    public MyNotificationManager(Context mCtx) {
        this.mCtx = mCtx;
        preferences = mCtx.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
    }

    //the method will show a big notification with an image
    //parameters are title for message title, message for message text, url of the big image and an intent that will open
    //when you will tap on the notification
    //  @RequiresApi(api = Build.VERSION_CODES.M)
    public void showBigNotification(String title, String message, String url, Intent intent) {
        if (preferences.getString("notifyonoff", "").equals("1")) {
            Random random1 = new Random();
            int m1 = random1.nextInt(9999 - 1000) + 1000;
            PendingIntent resultPendingIntent = PendingIntent.getActivity(mCtx, m1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.setBigContentTitle(title);
            bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
            bigPictureStyle.bigPicture(getBitmapFromURL(url));
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
            Notification notification;
            notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setStyle(bigPictureStyle)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.ic_launcher))
                    .setContentText(message)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();

            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;
            NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);

            // notificationManager.notify(m, mBuilder.build());


            notificationManager.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE), mBuilder.build());

/*

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText("New Message received");
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.setBigContentTitle(message);

        // Add your All messages here or use Loop to generate messages

        inboxStyle.addLine(message);
       // inboxStyle.addLine("Messgare 2");

      //  inboxStyle.addLine("Messgare n");


        mBuilder.setStyle(inboxStyle);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mCtx);

        stackBuilder.addNextIntent(intent);

        PendingIntent pIntent = PendingIntent.getActivity(mCtx, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pIntent);

        NotificationManager mNotificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(0, mBuilder.build());
*/
            setBadge();
        }

    }

    //the method will show a small notification
    //parameters are title for message title, message for message text and an intent that will open
    //when you will tap on the notification
    //  @RequiresApi(api = Build.VERSION_CODES.M)
/*    public void showSmallNotification(String title, String message, Intent intent) {
        Random random1 = new Random();
        int m1 = random1.nextInt(9999 - 1000) + 1000;
        PendingIntent resultPendingIntent = PendingIntent.getActivity(mCtx, m1, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.ic_launcher))
                .setContentText(message)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        //  notificationManager.notify(ID_SMALL_NOTIFICATION, notification);

        notificationManager.notify(m, notification);
        setBadge();
    }*/
    public void showSmallNotification(String title, String message, Intent intent) {
        if (preferences.getString("notifyonoff", "").equals("1")) {

            NotificationManager mNotificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID", "YOUR_CHANNEL_NAME", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
                mNotificationManager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx, "YOUR_CHANNEL_ID")

                    .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                    .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(title) // title for notification
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentText(message)// message for notification
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH); // clear notification after click
            Random random1 = new Random();
            int m1 = random1.nextInt(9999 - 1000) + 1000;
            PendingIntent pi = PendingIntent.getActivity(mCtx, m1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pi);
            mNotificationManager.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE), mBuilder.build());
            setBadge();


        }

    }
    //The method will return Bitmap from an image URL
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    //count show on app icon

    public void setBadge() {
        int c = preferences.getInt("n_count", 0);
        prefEditor.putInt("n_count", c + 1);
        prefEditor.commit();
        BadgeUtils.setBadge(mCtx,c);
        BadgeNumberManagerVIVO.setBadgeNumber(mCtx,c);

        String launcherClassName = getLauncherClassName(mCtx);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", preferences.getInt("n_count", 0));
        intent.putExtra("badge_count_package_name", mCtx.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        mCtx.sendBroadcast(intent);
    }

    public String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            String t=context.getPackageName();
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }
}