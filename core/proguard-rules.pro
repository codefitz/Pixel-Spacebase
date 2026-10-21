# Game scenes and saved objects are constructed reflectively. Class names also
# identify saved types and message keys, so retain names, constructors and members.
-keep class com.wafitz.pixelspacebase.** { *; }
-keep class com.watabou.** { *; }

# Retain source locations for meaningful crash reports.
-keepattributes SourceFile,LineNumberTable
