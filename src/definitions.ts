import { PluginListenerHandle } from "@capacitor/core";

declare module '@capacitor/core' {
  interface PluginRegistry {
    BilMobileAds: BilmobileAdsCapacitorPlugin;
  }
}

export interface BilmobileAdsCapacitorPlugin {
  addListener(eventName: 'BilmobileAdsPluginEvent', listenerFunc: AdEventListener): PluginListenerHandle;

  // PBM
  initialize(option: { testMode: boolean }): void;
  enableCOPPA(): void;
  disableCOPPA(): void;
  setYearOfBirth(option: { yearOfBirth: number }): void;
  setGender(option: { gender: BilGender }): void;

  // BANER
  createBanner(option: { adUnitId: string, position: AdPosition }): Promise<{ value: any }>;
  loadBanner(): Promise<{ value: any }>;
  showBanner(): Promise<{ value: any }>;
  hideBanner(): Promise<{ value: any }>;
  destroyBanner(): Promise<{ value: any }>;
  setPositionBanner(option: { position: AdPosition }): Promise<{ value: any }>;
  getSafeArea(): Promise<SafeArea>;

  // INTERSTITIAL
  createInterstitial(option: { adUnitId: string }): Promise<{ value: any }>;
  preLoadInterstitial(): Promise<{ value: any }>;
  showInterstitial(): Promise<{ value: any }>;
  destroyInterstitial(): Promise<{ value: any }>;
  isReadyInterstitial(): Promise<{ value: boolean }>;

  // REWARDED
  createRewarded(option: { adUnitId: string }): Promise<{ value: any }>;
  preLoadRewarded(): Promise<{ value: any }>;
  showRewarded(): Promise<{ value: any }>;
  destroyRewarded(): Promise<{ value: any }>;
  isReadyRewarded(): Promise<{ value: boolean }>;

}

export interface RewardedItem {
  typeRewarded: string;
  amountRewarded: number;
}

export interface SafeArea {
  topPadding: number;
  bottomPadding: number;
}

export interface AdEvents {
  adType: AdType;
  type: BilAdEvents;
  message: any;
}
export type AdEventListener = (status: AdEvents) => void;
export enum AdType {
  Banner = "Banner",
  Interstitial = "Interstitial",
  Rewarded = "Rewarded"
}
export enum AdPosition {
  TopCenter,
  TopLeft,
  TopRight,
  BottomCenter,
  BottomLeft,
  BottomRight,
  Center
}
export enum BilGender { Unknown, Male, Female }
export enum BilAdEvents {
  loaded,
  opened,
  closed,
  clicked,
  leftApplication,
  rewarded,
  failedToLoad,
  failedToShow,
}