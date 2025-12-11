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
        versionCode = 1
        versionName = "0.1.57"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            if (keystorePropertiesFile.exists()) {
                val keystoreProperties = Properties()
                FileInputStream(keystorePropertiesFile).use { keystoreProperties.load(it) }
                val storeFileProp = keystoreProperties.getProperty("storeFile")
                if (storeFileProp != null) {
                    // Resolve relative path from root project
                    val keystoreFile = if (storeFileProp.startsWith("/") || storeFileProp.matches(Regex("^[A-Za-z]:.*"))) {
                        file(storeFileProp)
                    } else {
                        rootProject.file(storeFileProp)
                    }
                    if (keystoreFile.exists()) {
                        storeFile = keystoreFile
                        storePassword = keystoreProperties.getProperty("storePassword")
                        keyAlias = keystoreProperties.getProperty("keyAlias")
                        keyPassword = keystoreProperties.getProperty("keyPassword")
                    }
                }
            } else if (project.hasProperty("MYAPP_RELEASE_STORE_FILE")) {
                val storeFileProp = project.findProperty("MYAPP_RELEASE_STORE_FILE") as String?
                if (storeFileProp != null) {
                    storeFile = file(storeFileProp)
                    storePassword = project.findProperty("MYAPP_RELEASE_STORE_PASSWORD") as String
                    keyAlias = project.findProperty("MYAPP_RELEASE_KEY_ALIAS") as String
                    keyPassword = project.findProperty("MYAPP_RELEASE_KEY_PASSWORD") as String
                }
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val releaseSigning = signingConfigs.findByName("release")
            signingConfig = if (releaseSigning?.storeFile != null && releaseSigning.storeFile?.exists() == true) {
                releaseSigning
            } else {
                signingConfigs.getByName("debug")
            }
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
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
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
}