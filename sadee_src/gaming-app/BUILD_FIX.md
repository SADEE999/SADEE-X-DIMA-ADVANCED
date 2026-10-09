# Build fix
- Added missing `android.content.pm.PackageManager` import.
- Replaced unsupported `ActivityManager.setProcessMemoryTrimLevel(...)` with Android-supported `onTrimMemory(...)`.
- PUBG multi-package launcher remains enabled.
