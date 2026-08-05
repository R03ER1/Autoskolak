import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "cz.autokolk"
    compileSdk = 36

    defaultConfig {
        applicationId = "cz.autokolk"
        minSdk = 24
        targetSdk = 36
        versionCode = 81
        versionName = "2.2.12"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            if (keystorePropertiesFile.exists()) {
                val keystoreProperties = Properties()
                FileInputStream(keystorePropertiesFile).use { keystoreProperties.load(it) }
                val storeFileProp = keystoreProperties.getProperty("storeFile")
                if (storeFileProp != null && storeFileProp.isNotBlank()) {
                    // Resolve relative path from root project
                    val keystoreFile = if (storeFileProp.startsWith("/") || storeFileProp.matches(Regex("^[A-Za-z]:.*"))) {
                        file(storeFileProp)
                    } else {
                        rootProject.file(storeFileProp)
                    }
                    if (keystoreFile.exists()) {
                        storeFile = keystoreFile
                        val storePasswordProp = keystoreProperties.getProperty("storePassword")
                        val keyAliasProp = keystoreProperties.getProperty("keyAlias")
                        val keyPasswordProp = keystoreProperties.getProperty("keyPassword")
                        
                        if (storePasswordProp != null && storePasswordProp.isNotBlank()) {
                            storePassword = storePasswordProp
                        }
                        if (keyAliasProp != null && keyAliasProp.isNotBlank()) {
                            keyAlias = keyAliasProp
                        }
                        if (keyPasswordProp != null && keyPasswordProp.isNotBlank()) {
                            keyPassword = keyPasswordProp
                        }
                    } else {
                        println("WARNING: Keystore file not found at: ${keystoreFile.absolutePath}")
                    }
                }
            } else if (project.hasProperty("MYAPP_RELEASE_STORE_FILE")) {
                val storeFileProp = project.findProperty("MYAPP_RELEASE_STORE_FILE") as String?
                if (storeFileProp != null) {
                    val keystoreFile = file(storeFileProp)
                    if (keystoreFile.exists()) {
                        storeFile = keystoreFile
                        storePassword = project.findProperty("MYAPP_RELEASE_STORE_PASSWORD") as String
                        keyAlias = project.findProperty("MYAPP_RELEASE_KEY_ALIAS") as String
                        keyPassword = project.findProperty("MYAPP_RELEASE_KEY_PASSWORD") as String
                    }
                }
            }
        }
    }

    buildTypes {
        debug {
            // Google's official AdMob test ad unit IDs — bezpečné pro dev/emulátor,
            // brání označení reálné traffic jako neplatné.
            buildConfigField("String", "ADMOB_INTERSTITIAL_ID", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "ADMOB_REWARDED_ID",     "\"ca-app-pub-3940256099942544/5224354917\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Produkční AdMob ad unit IDs — pouze pro release build.
            buildConfigField("String", "ADMOB_INTERSTITIAL_ID", "\"ca-app-pub-7904041740523292/1806063612\"")
            buildConfigField("String", "ADMOB_REWARDED_ID",     "\"ca-app-pub-7904041740523292/3817416182\"")
            val releaseSigning = signingConfigs.findByName("release")
            if (releaseSigning?.storeFile != null && releaseSigning.storeFile?.exists() == true) {
                signingConfig = releaseSigning
            } else {
                // Fallback to debug signing only for APK builds
                // Note: AAB generation requires a valid release signing config
                signingConfig = signingConfigs.getByName("debug")
            }
        }
    }
    
    // Bundle configuration
    bundle {
        language {
            enableSplit = false
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    
    setDynamicFeatures(setOf(":mediaassets"))
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.play.feature.delivery)
    implementation(libs.google.material)
    implementation(libs.gson)
    implementation(libs.commons.csv)
    implementation(libs.play.services.ads)
    implementation(libs.user.messaging.platform)
    implementation("androidx.fragment:fragment-ktx:1.8.2")
    implementation("androidx.biometric:biometric:1.1.0")

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.lottie.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.drawablepainter)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.haze)
    implementation(libs.haze.materials)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // LeakCanary — pouze pro debug build (memory leak detekce, krok 165 QA).
    // Automaticky se inicializuje přes vlastní ContentProvider, žádný kód v App.kt není potřeba.
    debugImplementation(libs.leakcanary.android)
}