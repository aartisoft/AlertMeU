package in.alertmeu.utils;

import android.content.Context;
import android.os.Build;

public class BadgeNumberManager {
    private Context mContext;

    private BadgeNumberManager(Context context) {
        mContext = context;
    }

    public static BadgeNumberManager from(Context context) {
        return new BadgeNumberManager(context);
    }

    private static final BadgeNumberManager.Impl IMPL;


    public void setBadgeNumber(int number) {
        IMPL.setBadgeNumber(mContext, number);
    }

    interface Impl {

        void setBadgeNumber(Context context, int number);

    }

    static class ImplHuaWei implements Impl {

        @Override
        public void setBadgeNumber(Context context, int number) {
            BadgeNumberManagerHuaWei.setBadgeNumber(context, number);
        }
    }

    static class ImplVIVO implements Impl {

        @Override
        public void setBadgeNumber(Context context, int number) {
            BadgeNumberManagerVIVO.setBadgeNumber(context, number);
        }
    }

    static class ImplOPPO implements Impl {

        @Override
        public void setBadgeNumber(Context context, int number) {
            BadgeNumberManagerOPPO.setBadgeNumber(context, number);
        }
    }

    static class ImplXiaomi implements Impl {

        @Override
        public void setBadgeNumber(Context context, int number) {
            //BadgeNumberManagerXiaomi.setBadgeNumber(context, number);
        }
    }

    static class ImplBase implements Impl {

        @Override
        public void setBadgeNumber(Context context, int number) {
            //do nothing

        }
    }

    static {
        String manufacturer = Build.MANUFACTURER;
        if (manufacturer.equalsIgnoreCase("Huawei")) {
            IMPL = new ImplHuaWei();
        } else if (manufacturer.equalsIgnoreCase("vivo")) {
            IMPL = new ImplVIVO();
        } else if (manufacturer.equalsIgnoreCase("OPPO")) {

            IMPL = new ImplOPPO();

        } else if (manufacturer.equalsIgnoreCase("Xiaomi")) {

            IMPL = new ImplXiaomi();

        } else {
            IMPL = new ImplBase();
        }
    }
}

