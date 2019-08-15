# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-ignorewarnings
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-dontoptimize
-dontpreverify
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class **.R$* {
    *;
}
-dontwarn android.support.**
-keepattributes Signature
-keepattributes EnclosingMethod
-keepclasseswithmembernames class * {
    native <methods>;
}
#================================================
# help us to debug
-renamesourcefileattribute SourceFile
-keepattributes Exceptions
-keepattributes SourceFile,LineNumberTable,keepattributes
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes Signature
-keepattributes *Annotation*
-dontshrink

# Config need by TinkerPatch
-keep class com.tinkerpatch.sdk.TinkerPatch { *; }
-keep class com.tinkerpatch.sdk.BuildConfig { *; }

-keep class com.tinkerpatch.sdk.TinkerPatch$Builder { *; }
-keep class com.tinkerpatch.sdk.server.RequestLoader { *; }
-keep class com.tinkerpatch.sdk.util.ContentLengthInputStream { *; }
-keep interface com.tinkerpatch.sdk.server.model.DataFetcher { *; }
-keep interface com.tinkerpatch.sdk.server.model.DataFetcher$DataCallback { *; }
-keep class com.tinkerpatch.sdk.server.model.TinkerClientUrl { *; }
-keep class com.tinkerpatch.sdk.server.callback.** { *; }
-keep class com.tinkerpatch.sdk.tinker.callback.** { *; }
-keep public class * extends android.app.Application
-keep class com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike { *; }
-keep class com.tencent.tinker.** { *; }

# Config from tinker
-dontwarn com.tencent.tinker.anno.AnnotationProcessor
-keep @com.tencent.tinker.anno.DefaultLifeCycle public class *
-keep public class * extends android.app.Application {
    *;
}

-keep public class com.tencent.tinker.loader.app.ApplicationLifeCycle {
    *;
}
-keep public class * implements com.tencent.tinker.loader.app.ApplicationLifeCycle {
    *;
}

-keep public class com.tencent.tinker.loader.TinkerLoader {
    *;
}
-keep public class * extends com.tencent.tinker.loader.TinkerLoader {
    *;
}
-keep public class com.tencent.tinker.loader.TinkerTestDexLoad {
    *;
}
-keep public class com.tencent.tinker.loader.TinkerTestAndroidNClassLoader {
    *;
}

#your dex.loader patterns here
-keep class com.hope.demo.SampleApplication
-keep class com.tencent.tinker.loader.**
-keep class com.hope.demo.TinkerMessageActivity
