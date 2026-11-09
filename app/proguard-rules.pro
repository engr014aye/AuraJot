# Add project specific ProGuard rules here.
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep models used in Room and Domain
-keep class com.sikandar.aurajot.data.local.** { *; }
-keep class com.sikandar.aurajot.domain.model.** { *; }

# Strip debug logging in release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}
