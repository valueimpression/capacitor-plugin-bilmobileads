#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(BilMobileAds, "BilMobileAds",
           CAP_PLUGIN_METHOD(initialize, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(enableCOPPA, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(disableCOPPA, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setYearOfBirth, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setGender, CAPPluginReturnPromise);
           
           CAP_PLUGIN_METHOD(createBanner, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(loadBanner, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(showBanner, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(hideBanner, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(destroyBanner, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setPositionBanner, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getSafeArea, CAPPluginReturnPromise);
           
           CAP_PLUGIN_METHOD(createInterstitial, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(preLoadInterstitial, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(showInterstitial, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(destroyInterstitial, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(isReadyInterstitial, CAPPluginReturnPromise);
           
           CAP_PLUGIN_METHOD(createRewarded, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(preLoadRewarded, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(showRewarded, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(destroyRewarded, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(isReadyRewarded, CAPPluginReturnPromise);
)
