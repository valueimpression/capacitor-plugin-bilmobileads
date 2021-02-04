package com.bilmobileads.ionic.capacitor;

import android.util.Log;
import android.view.Gravity;

class CAPUtils {
    static final String BannerType = "Banner";
    static final String InterstitialType = "Interstitial";
    static final String RewardedType = "Rewarded";

    static final int loaded = 0;
    static final int opened = 1;
    static final int closed = 2;
    static final int clicked = 3;
    static final int leftApplication = 4;
    static final int rewarded = 5;
    static final int failedToLoad = 6;
    static final int failedToShow = 7;

    static final int TopCenter = 0;
    static final int TopLeft = 1;
    static final int TopRight = 2;
    static final int BottomCenter = 3;
    static final int BottomLeft = 4;
    static final int BottomRight = 5;
    static final int Center = 6;

    static void log(String mess) {
        Log.d("CAPPBMobile", mess);
    }

    public static int getGravityForPositionCode(int positionCode) {
        int gravity;
        switch (positionCode) {
            case TopCenter:
                gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                break;
            case TopLeft:
                gravity = Gravity.TOP | Gravity.LEFT;
                break;
            case TopRight:
                gravity = Gravity.TOP | Gravity.RIGHT;
                break;
            case BottomCenter:
                gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                break;
            case BottomLeft:
                gravity = Gravity.BOTTOM | Gravity.LEFT;
                break;
            case BottomRight:
                gravity = Gravity.BOTTOM | Gravity.RIGHT;
                break;
            case Center:
                gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                break;
            default:
                throw new IllegalArgumentException("Attempted to position ad with invalid ad "
                        + "position.");
        }
        return gravity;
    }
}
