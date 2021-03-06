require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name = 'CapacitorPluginBilmobileads'
  s.version = package['version']
  s.summary = package['description']
  s.license = package['license']
  s.homepage = package['repository']['url']
  s.author = package['author']
  s.source = { :git => package['repository']['url'], :tag => s.version.to_s }
  s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'

  s.platforms = { :ios => "11.0" }
  s.swift_version = '5.0'

  s.static_framework = true

  s.dependency 'Capacitor'
  s.dependency "BilMobileAds", '1.2.1'
end
