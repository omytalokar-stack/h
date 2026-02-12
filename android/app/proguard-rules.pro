# Preserve line numbers for stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep all classes in our app
-keep class com.aiagent.app.** { *; }

# Preserve Google AI SDK
-keep class com.google.ai.** { *; }

# Keep Retrofit models
-keep class com.aiagent.app.model.** { *; }

# Keep OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Keep Gson
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Kotlin
-keep class kotlin.** { *; }
-dontwarn kotlin.**

# Preserve Enum
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
