Final compile fix:
- ActivityManager.onTrimMemory(...) was invalid.
- Uses MainActivity.this.onTrimMemory(...) because Activity implements ComponentCallbacks2.
