# Standard Android Attributes
-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod

# Retrofit
-keepattributes Signature, RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

# Gson
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Room
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Project DTOs (Keep everything with SerializedName)
-keepclassmembers class com.astrochat.feature.matches.data.remote.dto.** {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Hilt
-keep class com.google.dagger.** { *; }
-keep class dagger.hilt.** { *; }
-dontwarn com.google.dagger.**
-dontwarn dagger.hilt.**
