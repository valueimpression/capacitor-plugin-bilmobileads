//
//  CAPUtils.swift
//  CapacitorPluginBilmobileads
//
//  Created by HNL_MAC on 05/02/2021.
//

import Foundation

@objc public class CAPUtils: NSObject {
    static let bannerType = "Banner"
    static let interstitialType = "Interstitial"
    static let rewardedType = "Rewarded"
    
    static let loaded = 0
    static let opened = 1
    static let closed = 2
    static let clicked = 3
    static let leftApplication = 4
    static let rewarded = 5
    static let failedToLoad = 6
    static let failedToShow = 7
    
    static let TopCenter = 0
    static let TopLeft = 1
    static let TopRight = 2
    static let BottomCenter = 3
    static let BottomLeft = 4
    static let BottomRight = 5
    static let Center = 6
    
    @objc public static func log(mess: String) {
        print("CAPPBMobileAds -> \(mess)")
    }
}
