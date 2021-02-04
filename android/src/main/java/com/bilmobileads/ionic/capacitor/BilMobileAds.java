package com.bilmobileads.ionic.capacitor;

import android.graphics.Color;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bil.bilmobileads.ADBanner;
import com.bil.bilmobileads.ADInterstitial;
import com.bil.bilmobileads.ADRewarded;
import com.bil.bilmobileads.PBMobileAds;
import com.bil.bilmobileads.entity.ADRewardItem;
import com.bil.bilmobileads.interfaces.AdDelegate;
import com.bil.bilmobileads.interfaces.AdRewardedDelegate;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

@NativePlugin
public class BilMobileAds extends Plugin {

    static boolean isInitSucc = false;

    /**
     * PBM
     */
    @PluginMethod
    public void initialize(PluginCall call) {
        boolean testMode = call.getBoolean("testMode", true);

        getActivity().runOnUiThread(() -> {
            PBMobileAds.instance.initialize(getActivity(), testMode);
            this.isInitSucc = true;
            call.success();
        });
    }

    @PluginMethod
    public void enableCOPPA(PluginCall call) {
        PBMobileAds.instance.enableCOPPA();
        call.success();
    }

    @PluginMethod
    public void disableCOPPA(PluginCall call) {
        PBMobileAds.instance.disableCOPPA();
        call.success();
    }

    @PluginMethod
    public void setYearOfBirth(PluginCall call) {
        int yob = call.getInt("yearOfBirth");
        PBMobileAds.instance.setYearOfBirth(yob);
    }

    @PluginMethod
    public void setGender(PluginCall call) {
        int gender = call.getInt("gender");
        switch (gender) {
            case 0:
                PBMobileAds.instance.setGender(PBMobileAds.GENDER.UNKNOWN);
                break;
            case 1:
                PBMobileAds.instance.setGender(PBMobileAds.GENDER.MALE);
                break;
            case 2:
                PBMobileAds.instance.setGender(PBMobileAds.GENDER.FEMALE);
                break;
        }
    }

    JSObject getMess(String adType, int type, String mess) {
        JSObject data = new JSObject();
        data.put("adType", adType);
        data.put("type", type);
        data.put("message", mess);
        return data;
    }

    /**
     * BANNER
     */
    private final String BANNER_EVENT = "BilmobileAdsPluginEvent";

    private ADBanner adBanner;
    private RelativeLayout adPlaceholder;
    private int adPosition = CAPUtils.BottomCenter;

    //    private View.OnLayoutChangeListener viewChangeListener;

    /**
     * A boolean indicating whether the ad has been hidden.
     * true -> ad is off, false -> ad is on screen.
     */
    private boolean isHidden = false;

    @PluginMethod
    public void createBanner(PluginCall call) {
        if (!isInitSucc) {
            CAPUtils.log("PBMobileAds uninitialized, please call PBMobileAds.initialize() first.");
            return;
        }

        if (this.adBanner != null) {
            CAPUtils.log("ADBanner already exist");

            JSObject data = new JSObject();
            data.put("message", "ADBanner already exist");
            call.resolve(data);
            return;
        }

        String adUnitId = call.getString("adUnitId");
        if (adUnitId == null || adUnitId == "") {
            CAPUtils.log("adUnitId expected an string value and not empty");
            call.reject("adUnitId expected an string value and not empty");
            return;
        }

        int position = call.getInt("position");

        getActivity().runOnUiThread(() -> {
            this.isHidden = false;
            this.adPosition = position;

            createADPlaceholder();
            getActivity().addContentView(adPlaceholder,
                    new LinearLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT));

            //            viewChangeListener = (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            //                boolean viewBoundsChanged = left != oldLeft || right != oldRight || bottom != oldBottom || top != oldTop;
            //                if (!viewBoundsChanged) return;
            //                if (!isHidden) updateADPosition();
            //            };
            //            getActivity().getWindow()
            //                    .getDecorView()
            //                    .getRootView()
            //                    .addOnLayoutChangeListener(viewChangeListener);

            this.adBanner = new ADBanner(adPlaceholder, adUnitId);
            this.adBanner.setListener(new AdDelegate() {
                @Override
                public void onAdLoaded() {
                    updateADPosition();
                    notifyListeners(BANNER_EVENT, getMess(CAPUtils.BannerType, CAPUtils.loaded, null));
                }

                @Override
                public void onAdOpened() {
                    notifyListeners(BANNER_EVENT, getMess(CAPUtils.BannerType, CAPUtils.opened, null));
                }

                @Override
                public void onAdClosed() {
                    notifyListeners(BANNER_EVENT, getMess(CAPUtils.BannerType, CAPUtils.closed, null));
                }

                @Override
                public void onAdClicked() {
                    notifyListeners(BANNER_EVENT, getMess(CAPUtils.BannerType, CAPUtils.clicked, null));
                }

                @Override
                public void onAdLeftApplication() {
                    notifyListeners(BANNER_EVENT, getMess(CAPUtils.BannerType, CAPUtils.leftApplication, null));
                }

                @Override
                public void onAdFailedToLoad(final String errorCode) {
                    notifyListeners(BANNER_EVENT, getMess(CAPUtils.BannerType, CAPUtils.failedToLoad, errorCode));
                }
            });
            call.success();
        });
    }

    @PluginMethod
    public void loadBanner(PluginCall call) {
        if (!this.isPluginReady()) return;

        getActivity().runOnUiThread(() -> {
            if (!this.adBanner.isLoaded()) {
                this.adBanner.load();
            }
            call.resolve();
        });
    }

    @PluginMethod
    public void showBanner(PluginCall call) {
        if (!this.isPluginReady()) return;

        getActivity().runOnUiThread(() -> {
            this.isHidden = false;
            this.adPlaceholder.setVisibility(View.VISIBLE);
            this.adBanner.startFetchData();
            this.updateADPosition();

            call.resolve();
        });
    }

    @PluginMethod
    public void hideBanner(PluginCall call) {
        if (!this.isPluginReady()) return;

        getActivity().runOnUiThread(() -> {
            this.isHidden = true;
            this.adPlaceholder.setVisibility(View.GONE);
            this.adBanner.stopFetchData();

            call.resolve();
        });
    }

    @PluginMethod
    public void destroyBanner(PluginCall call) {
        if (!this.isPluginReady()) return;

        getActivity().runOnUiThread(() -> {
            adPlaceholder.removeAllViews();
            adPlaceholder.setVisibility(View.GONE);

            this.adBanner.destroy();
            this.adBanner = null;

//            getActivity().getWindow()
//                    .getDecorView()
//                    .getRootView()
//                    .removeOnLayoutChangeListener(viewChangeListener);

            call.resolve();
        });
    }

    @PluginMethod
    public void setPositionBanner(PluginCall call) {
        if (!this.isPluginReady()) return;

        getActivity().runOnUiThread(() -> {
            this.adPosition = call.getInt("position");
            this.updateADPosition();
            call.resolve();
        });
    }

    @PluginMethod
    public void getSafeArea(PluginCall call) {
        if (!this.isPluginReady()) return;

        getActivity().runOnUiThread(() -> {
            Insets insets = getSafeInsets();
            JSObject data = new JSObject();            
            int bannerH = this.adBanner.getHeight() > 0 ? this.adBanner.getHeight() : 50;
            data.put("topPadding", bannerH + insets.top);
            data.put("bottomPadding", bannerH + insets.bottom);
            call.resolve(data);
        });
    }

    @PluginMethod
    public void isReadyBanner(PluginCall call) {
        if (!this.isPluginReady()) return;

        JSObject data = new JSObject();
        data.put("isReady", this.adBanner.isLoaded());
        call.resolve(data);
    }

    boolean isPluginReady() {
        if (this.adBanner == null) {
            CAPUtils.log("ADBanner is nil. You need init ADBanner first.");
            return false;
        }
        return true;
    }

    private void createADPlaceholder() {
        // Create a RelativeLayout and add the ad view to it
        if (this.adPlaceholder == null) {
            this.adPlaceholder = new RelativeLayout(getActivity());
        } else {
            // Remove the layout if it has a parent
            FrameLayout parentView = (FrameLayout) this.adPlaceholder.getParent();
            if (parentView != null)
                parentView.removeView(this.adPlaceholder);
        }
        this.adPlaceholder.setBackgroundColor(Color.TRANSPARENT);
        this.adPlaceholder.setVisibility(View.VISIBLE);

        this.updateADPosition();
    }

    private void updateADPosition() {
        if (adPlaceholder == null || isHidden) return;

        getActivity().runOnUiThread(() -> {
            adPlaceholder.setLayoutParams(getLayoutParams());
        });
    }

    private FrameLayout.LayoutParams getLayoutParams() {
        Insets insets = getSafeInsets();

        final FrameLayout.LayoutParams adParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        adParams.gravity = CAPUtils.getGravityForPositionCode(this.adPosition);
        adParams.bottomMargin = insets.bottom;
        adParams.rightMargin = insets.right;
        adParams.leftMargin = insets.left;
        if (this.adPosition == CAPUtils.TopCenter || this.adPosition == CAPUtils.TopLeft || this.adPosition == CAPUtils.TopRight) {
            adParams.topMargin = insets.top;
        }

        return adParams;
    }

    /**
     * Class to hold the insets of the cutout area.
     */
    private static class Insets {
        int top = 0;
        int bottom = 0;
        int left = 0;
        int right = 0;
    }

    private Insets getSafeInsets() {
        Insets insets = new Insets();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return insets;
        }

        Window window = getActivity().getWindow();
        if (window == null) return insets;

        WindowInsets windowInsets = window.getDecorView().getRootWindowInsets();
        if (windowInsets == null) return insets;

        DisplayCutout displayCutout = windowInsets.getDisplayCutout();
        if (displayCutout == null) return insets;

        insets.top = displayCutout.getSafeInsetTop();
        insets.left = displayCutout.getSafeInsetLeft();
        insets.bottom = displayCutout.getSafeInsetBottom();
        insets.right = displayCutout.getSafeInsetRight();

        return insets;
    }

    /**
     * INTERSTITIAL
     */
    private final String INTERSTITIAL_EVENT = "BilmobileAdsPluginEvent";
    private ADInterstitial adInterstitial;

    @PluginMethod
    public void createInterstitial(PluginCall call) {
        if (!isInitSucc) {
            CAPUtils.log("PBMobileAds uninitialized, please call PBMobileAds.initialize() first.");
            return;
        }

        String adUnitId = call.getString("adUnitId");
        if (adUnitId == null || adUnitId == "") {
            CAPUtils.log("adUnitId expected an string value and not empty");
            call.reject("adUnitId expected an string value and not empty");
            return;
        }

        if (this.adInterstitial != null) {
            CAPUtils.log("AdInterstitial already exist");

            JSObject data = new JSObject();
            data.put("message", "AdInterstitial already exist");
            call.resolve(data);
            return;
        }

        getActivity().runOnUiThread(() -> {
            this.adInterstitial = new ADInterstitial(adUnitId);
            this.adInterstitial.setListener(new AdDelegate() {
                @Override
                public void onAdLoaded() {
                    notifyListeners(INTERSTITIAL_EVENT, getMess(CAPUtils.InterstitialType, CAPUtils.loaded, null));
                }

                @Override
                public void onAdOpened() {
                    notifyListeners(INTERSTITIAL_EVENT, getMess(CAPUtils.InterstitialType, CAPUtils.opened, null));
                }

                @Override
                public void onAdClosed() {
                    notifyListeners(INTERSTITIAL_EVENT, getMess(CAPUtils.InterstitialType, CAPUtils.closed, null));
                }

                @Override
                public void onAdClicked() {
                    notifyListeners(INTERSTITIAL_EVENT, getMess(CAPUtils.InterstitialType, CAPUtils.clicked, null));
                }

                @Override
                public void onAdLeftApplication() {
                    notifyListeners(INTERSTITIAL_EVENT, getMess(CAPUtils.InterstitialType, CAPUtils.leftApplication, null));
                }

                @Override
                public void onAdFailedToLoad(final String errorCode) {
                    notifyListeners(INTERSTITIAL_EVENT, getMess(CAPUtils.InterstitialType, CAPUtils.failedToLoad, errorCode));
                }
            });
            call.success();
        });
    }

    @PluginMethod
    public void preLoadInterstitial(PluginCall call) {
        if (!this.isFullReady()) return;

        getActivity().runOnUiThread(() -> {
            if (this.adInterstitial.isReady()) {
                CAPUtils.log("ADInterstitial is ready to show.");
            } else {
                this.adInterstitial.preLoad();
            }
            call.resolve();
        });
    }

    @PluginMethod
    public void showInterstitial(PluginCall call) {
        if (!this.isFullReady()) return;

        getActivity().runOnUiThread(() -> {
            if (!this.adInterstitial.isReady()) {
                CAPUtils.log("ADInterstitial currently unavailable, call preload() first.");
            } else {
                this.adInterstitial.show();
            }
            call.resolve();
        });
    }

    @PluginMethod
    public void destroyInterstitial(PluginCall call) {
        if (!this.isFullReady()) return;

        getActivity().runOnUiThread(() -> {
            this.adInterstitial.destroy();
            this.adInterstitial = null;
            call.resolve();
        });
    }

    @PluginMethod
    public void isReadyInterstitial(PluginCall call) {
        if (!this.isFullReady()) return;

        getActivity().runOnUiThread(() -> {
            JSObject data = new JSObject();
            data.put("isReady", this.adInterstitial.isReady());
            call.resolve(data);
        });
    }

    boolean isFullReady() {
        if (this.adInterstitial == null) {
            CAPUtils.log("ADInterstitial is nil. You need init ADInterstitial first.");
            return false;
        }
        return true;
    }

    /**
     * REWARDED
     */
    private final String REWARDED_EVENT = "BilmobileAdsPluginEvent";
    private ADRewarded adRewarded;

    @PluginMethod
    public void createRewarded(PluginCall call) {
        if (!isInitSucc) {
            CAPUtils.log("PBMobileAds uninitialized, please call PBMobileAds.initialize() first.");
            return;
        }

        String adUnitId = call.getString("adUnitId");
        if (adUnitId == null || adUnitId == "") {
            CAPUtils.log("adUnitId expected an string value and not empty");
            call.reject("adUnitId expected an string value and not empty");
            return;
        }

        if (this.adRewarded != null) {
            CAPUtils.log("ADRewarded already exist");

            JSObject data = new JSObject();
            data.put("message", "ADRewarded already exist");
            call.resolve(data);
            return;
        }

        getActivity().runOnUiThread(() -> {
            this.adRewarded = new ADRewarded(getActivity(), adUnitId);
            this.adRewarded.setListener(new AdRewardedDelegate() {
                @Override
                public void onRewardedAdLoaded() {
                    notifyListeners(REWARDED_EVENT, getMess(CAPUtils.RewardedType, CAPUtils.loaded, null));
                }

                @Override
                public void onRewardedAdOpened() {
                    notifyListeners(REWARDED_EVENT, getMess(CAPUtils.RewardedType, CAPUtils.opened, null));
                }

                @Override
                public void onRewardedAdClosed() {
                    notifyListeners(REWARDED_EVENT, getMess(CAPUtils.RewardedType, CAPUtils.closed, null));
                }

                @Override
                public void onUserEarnedReward(final ADRewardItem adRewardItem) {
                    JSObject data = new JSObject();
                    data.put("adType", CAPUtils.RewardedType);
                    data.put("type", CAPUtils.rewarded);

                    JSObject info = new JSObject();
                    info.put("typeRewarded", adRewardItem.getType());
                    info.put("amountRewarded", adRewardItem.getAmount());
                    data.put("message", info);
                    notifyListeners(REWARDED_EVENT, data);
                }

                @Override
                public void onRewardedAdFailedToLoad(final String error) {
                    notifyListeners(REWARDED_EVENT, getMess(CAPUtils.RewardedType, CAPUtils.failedToLoad, null));
                }

                @Override
                public void onRewardedAdFailedToShow(final String error) {
                    notifyListeners(REWARDED_EVENT, getMess(CAPUtils.RewardedType, CAPUtils.failedToShow, null));
                }
            });
            call.success();
        });
    }

    @PluginMethod
    public void preLoadRewarded(PluginCall call) {
        if (!this.isRewardedReady()) return;

        getActivity().runOnUiThread(() -> {
            if (this.adRewarded.isReady()) {
                CAPUtils.log("ADRewarded is ready to show.");
            } else {
                this.adRewarded.preLoad();
            }
            call.resolve();
        });
    }

    @PluginMethod
    public void showRewarded(PluginCall call) {
        if (!this.isRewardedReady()) return;

        getActivity().runOnUiThread(() -> {
            if (!this.adRewarded.isReady()) {
                CAPUtils.log("ADRewarded currently unavailable, call preload() first.");
            } else {
                this.adRewarded.show();
            }
            call.resolve();
        });
    }

    @PluginMethod
    public void destroyRewarded(PluginCall call) {
        if (!this.isRewardedReady()) return;

        getActivity().runOnUiThread(() -> {
            this.adRewarded.destroy();
            this.adRewarded = null;
            call.resolve();
        });
    }

    @PluginMethod
    public void isReadyRewarded(PluginCall call) {
        if (!this.isRewardedReady()) return;

        getActivity().runOnUiThread(() -> {
            JSObject data = new JSObject();
            data.put("isReady", this.adRewarded.isReady());
            call.resolve(data);
        });
    }

    boolean isRewardedReady() {
        if (this.adRewarded == null) {
            CAPUtils.log("ADRewarded is nil. You need init ADRewarded first.");
            return false;
        }
        return true;
    }
}
