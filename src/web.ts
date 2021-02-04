import { WebPlugin } from '@capacitor/core';
import { AdPosition, BilGender, BilmobileAdsCapacitorPlugin, SafeArea } from './definitions';

export class BilmobileAdsCapacitorWeb extends WebPlugin implements BilmobileAdsCapacitorPlugin {
  constructor() {
    super({
      name: 'BilmobileAdsCapacitor',
      platforms: ['web'],
    });
  }
  initialize(option: { testMode: boolean; }): void {
    console.log(option);
    throw new Error('Method not implemented.');
  }
  enableCOPPA(): void {
    throw new Error('Method not implemented.');
  }
  disableCOPPA(): void {
    throw new Error('Method not implemented.');
  }
  setYearOfBirth(option: { yearOfBirth: number; }): void {
    console.log(option);
    throw new Error('Method not implemented.');
  }
  setGender(option: { gender: BilGender; }): void {
    console.log(option);
    throw new Error('Method not implemented.');
  }
  createBanner(option: { adUnitId: string; position: AdPosition; }): Promise<{ value: any; }> {
    console.log(option);
    throw new Error('Method not implemented.');
  }
  loadBanner(): Promise<{ value: any; }> {
    throw new Error('Method not implemented.');
  }
  showBanner(): Promise<{ value: any; }> {
    throw new Error('Method not implemented.');
  }
  hideBanner(): Promise<{ value: any; }> {
    throw new Error('Method not implemented.');
  }
  destroyBanner(): Promise<{ value: any; }> {
    throw new Error('Method not implemented.');
  }
  setPositionBanner(option: { position: AdPosition; }): Promise<{ value: any; }> {
    console.log(option);
    throw new Error('Method not implemented.');
  }
  getSafeArea(): Promise<SafeArea> {
    throw new Error('Method not implemented.');
  }
  createInterstitial(option: { adUnitId: string; }): Promise<{ value: any; }> {
    console.log(option);
    throw new Error('Method not implemented.');
  }
  preLoadInterstitial(): Promise<{ value: any; }> {
    throw new Error('Method not implemented.');
  }
  showInterstitial(): Promise<{ value: any; }> {
    throw new Error('Method not implemented.');
  }
  destroyInterstitial(): Promise<{ value: any; }> {
    throw new Error('Method not implemented.');
  }
  isReadyInterstitial(): Promise<{ value: boolean; }> {
    throw new Error('Method not implemented.');
  }
  createRewarded(option: { adUnitId: string; }): Promise<{ value: any; }> {
    console.log(option);
    throw new Error('Method not implemented.');
  }
  preLoadRewarded(): Promise<{ value: any; }> {
    throw new Error('Method not implemented.');
  }
  showRewarded(): Promise<{ value: any; }> {
    throw new Error('Method not implemented.');
  }
  destroyRewarded(): Promise<{ value: any; }> {
    throw new Error('Method not implemented.');
  }
  isReadyRewarded(): Promise<{ value: boolean; }> {
    throw new Error('Method not implemented.');
  }
}

const BilmobileAdsCapacitor = new BilmobileAdsCapacitorWeb();

export { BilmobileAdsCapacitor };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(BilmobileAdsCapacitor);
