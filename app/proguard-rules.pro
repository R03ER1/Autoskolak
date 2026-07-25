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

# Gson (LessonProgress and similar)
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Gson-reflected model classes (krok 162): žádná z nich používá @SerializedName, takže
# Gson mapuje JSON klíče na názvy polí za běhu a instance vznikají čistě reflexí
# (TypeToken + UnsafeAllocator), bez jediného přímého "new" volání v kódu. Proto NESTAČÍ
# "-keepclassmembers" (ten chrání jen pole/metody, POKUD třída už přežije shrinking z
# jiného důvodu) — R8 takové "jen reflexí používané" třídy vyhodnotí jako bezpečné ke
# class-mergingu/odstranění, Gson pak vrátí objekt jiného runtime typu a appka spadne
# s ClassCastException hned při startu (viz 2.1.0 → hotfix). Musí být "-keep class",
# které navíc zachová identitu/strukturu celé třídy a zabrání jejímu sloučení s jinou.
-keep class cz.autokolk.Question { *; }
-keep class cz.autokolk.LessonProgress$LessonStateJson { *; }
-keep class cz.autokolk.LessonProgress$PracticeStore { *; }

# Play Feature Delivery / Split Install
-keep class com.google.android.play.core.splitinstall.** { *; }
-dontwarn com.google.android.play.core.**

# AdMob / ads identifiers
-keep public class com.google.android.gms.ads.** { *; }
-keep public class com.google.ads.** { *; }

# User Messaging Platform (consent)
-keep class com.google.android.ump.** { *; }

# Apache Commons CSV (used for questions)
-keep class org.apache.commons.csv.** { *; }