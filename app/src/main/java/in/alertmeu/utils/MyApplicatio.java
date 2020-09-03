package in.alertmeu.utils;

import android.app.Application;

public class MyApplicatio extends Application {

     static MyApplicatio mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

   /* public static MyApplicatio getContext() {
        return mContext;
    }*/

    public static synchronized MyApplicatio getInstance() {
        return mContext;
    }

   /* public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }*/
}