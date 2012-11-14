// Module script for production
import kotlin.modules.*
fun project() {
    module("AndroidRivers") {
        sources += "C:/Users/Dody/Documents/GitHub/AndroidRivers/src/com/silverkeytech/android_rivers/MainActivity.kt"
        // Boot classpath
        classpath += "C:/Program Files (x86)/Android/android-sdk/platforms/android-8/android.jar"
        classpath += "C:/Program Files (x86)/Android/android-sdk/platforms/android-8/data/res"
        classpath += "C:/Program Files (x86)/Android/android-sdk/tools/support/annotations.jar"
        // Compilation classpath
        // Output directory, commented out
        //         classpath += "C:/Users/Dody/Documents/GitHub/AndroidRivers/out/test/AndroidRivers"
        // Output directory, commented out
        //         classpath += "C:/Users/Dody/Documents/GitHub/AndroidRivers/out/production/AndroidRivers"
        classpath += "C:/Users/Dody/Documents/GitHub/AndroidRivers/lib/kotlin-runtime.jar"
        // Java classpath (for Java sources)
        classpath += "C:/Users/Dody/.IntelliJIdea12/system/compiler/androidrivers.b8c3fd44/.generated/Android_PNG_files_caching_compiler/AndroidRivers.ddc84472/production"
        classpath += "C:/Users/Dody/.IntelliJIdea12/system/compiler/androidrivers.b8c3fd44/.generated/Android_PNG_files_caching_compiler/AndroidRivers.ddc84472/test"
        classpath += "C:/Users/Dody/.IntelliJIdea12/system/compiler/androidrivers.b8c3fd44/.generated/aidl/AndroidRivers.ddc84472/production"
        classpath += "C:/Users/Dody/.IntelliJIdea12/system/compiler/androidrivers.b8c3fd44/.generated/aidl/AndroidRivers.ddc84472/test"
        classpath += "C:/Users/Dody/Documents/GitHub/AndroidRivers/src"
        classpath += "C:/Users/Dody/.IntelliJIdea12/system/compiler/androidrivers.b8c3fd44/.generated/Android_Including_Compiler/AndroidRivers.ddc84472/production"
        classpath += "C:/Users/Dody/.IntelliJIdea12/system/compiler/androidrivers.b8c3fd44/.generated/Android_Including_Compiler/AndroidRivers.ddc84472/test"
        classpath += "C:/Users/Dody/.IntelliJIdea12/system/compiler/androidrivers.b8c3fd44/.generated/Android_Renderscript_Compiler/AndroidRivers.ddc84472/production"
        classpath += "C:/Users/Dody/.IntelliJIdea12/system/compiler/androidrivers.b8c3fd44/.generated/Android_Renderscript_Compiler/AndroidRivers.ddc84472/test"
        classpath += "C:/Users/Dody/.IntelliJIdea12/system/compiler/androidrivers.b8c3fd44/.generated/Android_BuildConfig_Generator/AndroidRivers.ddc84472/production"
        classpath += "C:/Users/Dody/.IntelliJIdea12/system/compiler/androidrivers.b8c3fd44/.generated/Android_BuildConfig_Generator/AndroidRivers.ddc84472/test"
        classpath += "C:/Users/Dody/.IntelliJIdea12/system/compiler/androidrivers.b8c3fd44/.generated/aapt/AndroidRivers.ddc84472/production"
        classpath += "C:/Users/Dody/.IntelliJIdea12/system/compiler/androidrivers.b8c3fd44/.generated/aapt/AndroidRivers.ddc84472/test"
        // Main output
        // External annotations
        annotationsPath += "C:/Users/Dody/.IntelliJIdea12/config/plugins/Kotlin/kotlinc/lib/kotlin-jdk-annotations.jar"
    }
}
