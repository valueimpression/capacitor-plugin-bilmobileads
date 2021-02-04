import Foundation
import Capacitor
import BilMobileAds

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(BilMobileAds)
public class BilMobileAds: CAPPlugin, ADBannerDelegate, ADInterstitialDelegate, ADRewardedDelegate {

    // MARK: - PBM
    func getUIViewController() -> UIViewController {
        return (UIApplication.shared.keyWindow?.rootViewController)!
    }
    
    @objc func initialize(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            let testMode = call.getBool("testMode") ?? true
            PBMobileAds.shared.initialize(testMode: testMode)
        }
    }
    
    @objc func enableCOPPA(_ call: CAPPluginCall) {
        PBMobileAds.shared.enableCOPPA()
    }
    
    @objc func disableCOPPA(_ call: CAPPluginCall) {
        PBMobileAds.shared.disableCOPPA()
    }
    
    @objc func setYearOfBirth(_ call: CAPPluginCall) {
        guard let yearOfBirth = call.getInt("yearOfBirth") else {
            return;
        }
        PBMobileAds.shared.setYearOfBirth(yob: yearOfBirth)
    }
    
    @objc func setGender(_ call: CAPPluginCall) {
        let gender = call.getInt("gender") ?? 0
        switch gender {
        case 0:
            PBMobileAds.shared.setGender(gender: .unknown);
        case 1:
            PBMobileAds.shared.setGender(gender: .male);
        case 2:
            PBMobileAds.shared.setGender(gender: .female);
        default:
            PBMobileAds.shared.setGender(gender: .unknown);
        }
    }

    // MARK: - BANNER
    private var banner: ADBanner!
    private var adPlacehold: UIViewController!
    private let BANNER_EVENT = "BilmobileAdsPluginEvent"
    private var adPosition: Int = CAPUtils.BottomCenter
    
    @objc func createBanner(_ call: CAPPluginCall) {
        if self.banner != nil {
            CAPUtils.log(mess: "ADBanner already exist")
            call.resolve(["message": "ADBanner already exist"])
            return
        }
        
        let adUnitId = call.getString("adUnitId") ?? ""
        if adUnitId.isEmpty {
            CAPUtils.log(mess: "adUnitId expected an string value and not empty")
            call.reject("adUnitId expected an string value and not empty")
            return
        }
        
        self.adPosition = call.getInt("position") ?? CAPUtils.BottomCenter

        DispatchQueue.main.async {
            let uvCtrl = self.getUIViewController()
            self.banner = ADBanner(uvCtrl, view: uvCtrl.view, placement: adUnitId)
            self.banner.setListener(self)
            self.banner.isDisSetupAnchor(true)
            
            call.resolve()
        }
    }
    
    @objc func loadBanner(_ call: CAPPluginCall) {
        if !self.isPluginReady() { return }
        
        DispatchQueue.main.async {
            if !self.banner.isLoaded() {
                self.banner.load()
            }
            call.resolve()
        }
    }
    
    @objc func showBanner(_ call: CAPPluginCall) {
        if !self.isPluginReady() { return }
        
        DispatchQueue.main.async {
            if self.banner.isLoaded() {
                self.banner.getADView().isHidden = false
                self.banner.startFetchData()
            }
            call.resolve()
        }
    }
    
    @objc func hideBanner(_ call: CAPPluginCall) {
        if !self.isPluginReady() { return }
        
        DispatchQueue.main.async {
            if self.banner.isLoaded() {
                self.banner.getADView().isHidden = true
                self.banner.stopFetchData()
            }
            call.resolve()
        }
    }
    
    @objc func destroyBanner(_ call: CAPPluginCall) {
        if !self.isPluginReady() { return }
        
        self.banner.destroy()
        self.banner = nil;
        call.resolve()
    }
    
    @objc func setPositionBanner(_ call: CAPPluginCall) {
        if !self.isPluginReady() { return }
        
        DispatchQueue.main.async {
            self.adPosition = call.getInt("position") ?? CAPUtils.BottomCenter
            self.setupPosition(positionView: self.banner.getADView(), inParentView: self.getUIViewController().view, adPosition: self.adPosition)
            call.resolve()
        }
    }
    
    @objc func getSafeArea(_ call: CAPPluginCall) {
        if !self.isPluginReady() { return }
        
        DispatchQueue.main.async {
            let window = UIApplication.shared.keyWindow
            let topPadding = window?.safeAreaInsets.top ?? 0
            let bottomPadding = window?.safeAreaInsets.bottom ?? 0
            CAPUtils.log(mess: "topPadding: \(topPadding) | bottomPadding: \(bottomPadding)")
            let bannerH = self.banner.getADView().frame.size.height
            call.resolve([
                "topPadding": bannerH + topPadding,
                "bottomPadding": bannerH +  bottomPadding
            ])
        }
    }
        
    func isPluginReady() -> Bool {
        if self.banner == nil {
            CAPUtils.log(mess: "ADBanner is nil. You need init ADBanner first.")
            return false
        }
        return true
    }
    
    func setupPosition(positionView view: UIView, inParentView parentView: UIView, adPosition: Int) {
        var parentBounds: CGRect = parentView.bounds;
        if #available(iOS 11, *) {
            let safeAreaFrame: CGRect = parentView.safeAreaLayoutGuide.layoutFrame;
            if !CGSize.zero.equalTo(safeAreaFrame.size) {
                parentBounds = safeAreaFrame;
            }
        }
        
        var top: CGFloat = parentBounds.minY + view.bounds.midY
        var left: CGFloat = parentBounds.minX  + view.bounds.midX
        
        let bottom: CGFloat = parentBounds.maxY - view.bounds.midY
        let right: CGFloat = parentBounds.maxX - view.bounds.midX
        let centerX: CGFloat = parentBounds.midX
        let centerY: CGFloat = parentBounds.midY
        
        /// If this view is of greater or equal width to the parent view, do not offset
        /// to edge of safe area. Eg for smart banners that are still full screen width.
        if (view.bounds.width >= parentView.bounds.width) {
            left = parentView.bounds.midX
        }
        
        /// Similarly for height, if view is of custom size which is full screen height, do not offset.
        if (view.bounds.height >= parentView.bounds.height) {
            top = parentView.bounds.midY
        }
        
        var center: CGPoint = CGPoint(x: centerX, y: top)
        switch (adPosition) {
        case CAPUtils.TopCenter:
            center = CGPoint(x: centerX, y: top)
            break;
        case CAPUtils.TopLeft:
            center = CGPoint(x: left, y: top);
            break;
        case CAPUtils.TopRight:
            center = CGPoint(x: right, y: top);
            break;
        case CAPUtils.BottomCenter:
            center = CGPoint(x: centerX, y: bottom);
            break;
        case CAPUtils.BottomLeft:
            center = CGPoint(x: left, y: bottom);
            break;
        case CAPUtils.BottomRight:
            center = CGPoint(x: right, y: bottom);
            break;
        case CAPUtils.Center:
            center = CGPoint(x: centerX, y: centerY);
            break;
        default:
            break;
        }
        view.center = center;
    }
    
    // MARK: - Banner Delegate
    public func bannerDidReceiveAd() {
        self.setupPosition(positionView: banner.getADView(), inParentView: self.getUIViewController().view, adPosition: self.adPosition)
        self.notifyListeners(self.BANNER_EVENT, data: ["adType": CAPUtils.bannerType, "type": CAPUtils.loaded, "message": ""])
    }
    
    public func bannerWillPresentScreen() {
        self.notifyListeners(self.BANNER_EVENT, data: ["adType": CAPUtils.bannerType, "type": CAPUtils.opened, "message": ""])
    }
    
    public func bannerWillDismissScreen() {}
    
    public func bannerDidDismissScreen() {
        self.notifyListeners(self.BANNER_EVENT, data: ["adType": CAPUtils.bannerType, "type": CAPUtils.closed, "message": ""])
    }
    
    public func bannerWillLeaveApplication() {
        self.notifyListeners(self.BANNER_EVENT, data: ["adType": CAPUtils.bannerType, "type": CAPUtils.clicked, "message": ""])
        self.notifyListeners(self.BANNER_EVENT, data: ["adType": CAPUtils.bannerType, "type": CAPUtils.leftApplication, "message": ""])
    }
    
    public func bannerLoadFail(error: String) {
        self.notifyListeners(self.BANNER_EVENT, data: ["adType": CAPUtils.bannerType, "type": CAPUtils.failedToLoad, "message": error])
    }
    
    // MARK: - INTERSTITIAL
    private var interstitial: ADInterstitial!
    private let INTERSTITIAL_EVENT = "BilmobileAdsPluginEvent"
    
    @objc func createInterstitial(_ call: CAPPluginCall) {
        let adUnitId = call.getString("adUnitId") ?? ""
        if adUnitId.isEmpty {
            CAPUtils.log(mess: "adUnitId expected an string value and not empty")
            call.reject("adUnitId expected an string value and not empty")
            return
        }
        
        if self.interstitial != nil {
            CAPUtils.log(mess: "AdInterstitial already exist")
            call.resolve(["message": "AdInterstitial already exist"])
            return
        }
        
        DispatchQueue.main.async {
            self.interstitial = ADInterstitial(self.getUIViewController(), placement: adUnitId)
            self.interstitial.setListener(self)
            call.resolve()
        }
    }
    
    @objc func preLoadInterstitial(_ call: CAPPluginCall) {
        if !self.isFullReady() { return }
        
        DispatchQueue.main.async {
            if self.interstitial.isReady() {
                CAPUtils.log(mess: "ADInterstitial is ready to show.")
            } else {
                self.interstitial.preLoad()
            }
            call.resolve()
        }
    }
    
    @objc func showInterstitial(_ call: CAPPluginCall) {
        if !self.isFullReady() { return }
        
        DispatchQueue.main.async {
            if !self.interstitial.isReady() {
                CAPUtils.log(mess: "ADInterstitial currently unavailable, call preload() first.")
            } else {
                self.interstitial.show()
            }
            call.resolve()
        }
    }
    
    @objc func destroyInterstitial(_ call: CAPPluginCall) {
        if !self.isFullReady() { return }
        
        self.interstitial.destroy()
        self.interstitial = nil;
        call.resolve()
    }
    
    @objc func isReadyInterstitial(_ call: CAPPluginCall) {
        if !self.isPluginReady() { return }
        
        call.resolve([
            "isReady": interstitial.isReady()
        ])
    }
    
    func isFullReady() -> Bool {
        if self.interstitial == nil {
            CAPUtils.log(mess: "ADInterstitial is nil. You need init ADInterstitial first.")
            return false
        }
        return true
    }
    
    // MARK: - Interstitial Delegate
    public func interstitialDidReceiveAd() {
        self.notifyListeners(self.INTERSTITIAL_EVENT, data: ["adType": CAPUtils.interstitialType, "type": CAPUtils.loaded, "message": ""])
    }
    
    public func interstitialLoadFail(error: String) {
        self.notifyListeners(self.INTERSTITIAL_EVENT, data: ["adType": CAPUtils.interstitialType, "type": CAPUtils.failedToLoad, "message": error])
    }
    
    public func interstitialWillPresentScreen() {
        self.notifyListeners(self.INTERSTITIAL_EVENT, data: ["adType": CAPUtils.interstitialType, "type": CAPUtils.opened, "message": ""])
    }
    
    public func interstitialDidFailToPresentScreen() {
        self.notifyListeners(self.INTERSTITIAL_EVENT, data: ["adType": CAPUtils.interstitialType, "type": CAPUtils.failedToShow, "message": ""])
    }
    
    public func interstitialWillDismissScreen() {}
    
    public func interstitialDidDismissScreen() {
        self.notifyListeners(self.INTERSTITIAL_EVENT, data: ["adType": CAPUtils.interstitialType, "type": CAPUtils.closed, "message": ""])
    }
    
    public func interstitialWillLeaveApplication() {
        self.notifyListeners(self.INTERSTITIAL_EVENT, data: ["adType": CAPUtils.interstitialType, "type": CAPUtils.clicked, "message": ""])
        self.notifyListeners(self.INTERSTITIAL_EVENT, data: ["adType": CAPUtils.interstitialType, "type": CAPUtils.leftApplication, "message": ""])
    }
    
    // MARK: - REWARDED
    private var rewarded: ADRewarded!
    private let REWARDED_EVENT = "BilmobileAdsPluginEvent"
    
    @objc func createRewarded(_ call: CAPPluginCall) {
        let adUnitId = call.getString("adUnitId") ?? ""
        if adUnitId.isEmpty {
            CAPUtils.log(mess: "adUnitId expected an string value and not empty")
            call.reject("adUnitId expected an string value and not empty")
            return
        }
        
        if self.rewarded != nil {
            CAPUtils.log(mess: "Adrewarded already exist")
            call.resolve(["message": "Adrewarded already exist"])
            return
        }
        
        DispatchQueue.main.async {
            self.rewarded = ADRewarded(self.getUIViewController(), placement: adUnitId)
            self.rewarded.setListener(self)
            call.resolve()
        }
    }
    
    @objc func preLoadRewarded(_ call: CAPPluginCall) {
        if !self.isRewardedReady() { return }
        
        DispatchQueue.main.async {
            if self.rewarded.isReady() {
                CAPUtils.log(mess: "ADrewarded is ready to show.")
            } else {
                self.rewarded.preLoad()
            }
            call.resolve()
        }
    }
    
    @objc func showRewarded(_ call: CAPPluginCall) {
        if !self.isRewardedReady() { return }
        
        DispatchQueue.main.async {
            if !self.rewarded.isReady() {
                CAPUtils.log(mess: "ADrewarded currently unavailable, call preload() first.")
            } else {
                self.rewarded.show()
            }
            call.resolve()
        }
    }
    
    @objc func destroyRewarded(_ call: CAPPluginCall) {
        if !self.isRewardedReady() { return }
        
        self.rewarded.destroy()
        self.rewarded = nil;
        call.resolve()
    }
    
    @objc func isReadyRewarded(_ call: CAPPluginCall) {
        if !self.isRewardedReady() { return }
        
        call.resolve([
            "isReady": rewarded.isReady()
        ])
    }
    
    func isRewardedReady() -> Bool {
        if self.rewarded == nil {
            CAPUtils.log(mess: "ADrewarded is nil. You need init ADrewarded first.")
            return false
        }
        return true
    }
    
    // MARK: - Rewarded Delegate
    public func rewardedDidReceiveAd() {
        self.notifyListeners(self.REWARDED_EVENT, data: ["adType": CAPUtils.rewardedType, "type": CAPUtils.loaded, "message": ""])
    }
    
    public func rewardedDidPresent() {
        self.notifyListeners(self.REWARDED_EVENT, data: ["adType": CAPUtils.rewardedType, "type": CAPUtils.opened, "message": ""])
    }
    
    public func rewardedDidDismiss() {
        self.notifyListeners(self.REWARDED_EVENT, data: ["adType": CAPUtils.rewardedType, "type": CAPUtils.closed, "message": ""])
    }
    
    public func rewardedUserDidEarn(rewardedItem: ADRewardedItem) {
        self.notifyListeners(self.REWARDED_EVENT, data: ["adType": CAPUtils.rewardedType, "type": CAPUtils.rewarded, "message": ["typeRewarded": rewardedItem.getType(), "amountRewarded": rewardedItem.getAmount()]])
    }
    
    public func rewardedFailedToLoad(error: String) {
        self.notifyListeners(self.REWARDED_EVENT, data: ["adType": CAPUtils.rewardedType, "type": CAPUtils.failedToLoad, "message": error])
    }
    
    public func rewardedFailedToPresent(error: String) {
        self.notifyListeners(self.REWARDED_EVENT, data: ["adType": CAPUtils.rewardedType, "type": CAPUtils.failedToShow, "message": error])
    }
    
}
