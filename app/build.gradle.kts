import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "cz.autokolk"
    compileSdk = 35

    defaultConfig {
        applicationId = "cz.autokolk"
        minSdk = 24
        targetSdk = 35
        versionCode = 21
        versionName = "2.0.18"

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
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    
    setDynamicFeatures(setOf(
        ":imageassets",
        ":videoassets1",
        ":videoassets2",
        ":videoassets3",
        ":videoassets4",
        ":videoassets5"
    ))
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // WorkManager for background scheduling (use 2.8.1 for broad compatibility)
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    // Play Feature Delivery for dynamic feature modules
    implementation("com.google.android.play:feature-delivery:2.1.0")
    // Material Components for View system (not Compose)
    implementation("com.google.android.material:material:1.12.0")
    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")
    // Apache Commons CSV for CSV parsing
    implementation("org.apache.commons:commons-csv:1.10.0")
    // Google Mobile Ads SDK (AdMob)
    implementation("com.google.android.gms:play-services-ads:23.5.0")
    // User Messaging Platform (GDPR consent form before personalized ads)
    implementation("com.google.android.ump:user-messaging-platform:3.1.0")

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.lottie.compose)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}