export const IS_LINUX = /Linux/.test(navigator.platform)
export const IS_WINDOWS = ["Win32", "Win64", "Windows", "WinCE"].indexOf(navigator.platform) !== -1
export const IS_MAC_OS = ["Macintosh", "MacIntel", "MacPPC", "Mac68K"].indexOf(navigator.platform) !== -1
export const IS_IOS = ["iPhone", "iPad", "iPod"].indexOf(navigator.platform) !== -1
export const IS_ANDROID = /Android/.test(navigator.userAgent)
export const IS_MOBILE = IS_IOS || IS_ANDROID
export const IS_FIREFOX = /(?:firefox|fxios)\/\d+/i.test(navigator.userAgent)
export const IS_SAFARI = /^((?!chrome|android).)*safari/i.test(navigator.userAgent)
export const IS_CHROME_IOS = /CriOS\/[\d]+/.test(navigator.userAgent)
export const IS_WEBVIEW_MAC_OS = /Macintosh/.test(navigator.userAgent) && /AppleWebKit/.test(navigator.userAgent) && !/Safari/.test(navigator.userAgent)

export const IS_WORKER_SUPPORTED = "Worker" in self
export const IS_SERVICE_WORKER_SUPPORTED = "serviceWorker" in navigator
export const IS_ANCHOR_DOWNLOAD_SUPPORTED = self.document && "download" in HTMLAnchorElement.prototype && !IS_WEBVIEW_MAC_OS
export const IS_TOUCH_SUPPORTED = self.document && self.matchMedia("(pointer: coarse)").matches
export const IS_NOTIFICATIONS_SUPPORTED = "Notification" in self
export const IS_WEB_PUSH_SUPPORTED = IS_SERVICE_WORKER_SUPPORTED && IS_NOTIFICATIONS_SUPPORTED && "showNotification" in ServiceWorkerRegistration.prototype && "PushManager" in self
export const IS_MEDIA_ENUMERATE_DEVICES_SUPPORTED = "mediaDevices" in navigator && "enumerateDevices" in navigator.mediaDevices
export const IS_USER_MEDIA_SUPPORTED = "mediaDevices" in navigator && "getUserMedia" in navigator.mediaDevices
export const IS_DISPLAY_MEDIA_SUPPORTED = "mediaDevices" in navigator && "getDisplayMedia" in navigator.mediaDevices
export const IS_MEDIA_OUTPUT_SUPPORTED = self.document && "setSinkId" in HTMLMediaElement.prototype
export const IS_CANVAS_FILTER_SUPPORTED = self.document && "filter" in (document.createElement("canvas").getContext("2d") || {})
export const IS_CLIPBOARD_SUPPORTED = navigator.clipboard
export const IS_CLIPBOARD_ITEM_SUPPORTED = navigator.clipboard && typeof self.ClipboardItem !== "undefined"
