plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-kapt") // Add this line for Kapt
}

android {
    namespace = "com.example.tree"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tree"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        dataBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7" // Ensure the correct version is used
    }

    sourceSets {
        getByName("main").java.srcDirs("build/generated/source/navigation-args")
    }

    dataBinding {
        enable = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.firebase.storage)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.auth)

    implementation(libs.play.services.auth)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.activity.v190)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.runtime.livedata)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.multidex)

    // Kotlin
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.compose)

    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))
    // Declare the dependencies for the Firebase Cloud Firestore and Analytics libraries
    implementation(libs.firebase.firestore)
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.glide)
    kapt(libs.compiler) // Ensure you are using kapt for Glide
    // Lifecycle
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Activity KTX
    implementation(libs.androidx.activity.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    //
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.circleimageview)

    // PhotoView
    implementation(libs.photoview)

    // ViewPager2
    implementation(libs.androidx.viewpager2)

    // Compose
    implementation(libs.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.material.icons.extended)
    implementation(libs.constraintlayout.compose)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    implementation(libs.glide)
    kapt("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.github.bumptech.glide:compose:1.0.0-alpha.1")

    // Coil for Jetpack Compose
    implementation("io.coil-kt:coil-compose:2.0.0")
    // Hilt dependencies
    implementation("com.google.dagger:hilt-android:2.38.1")
    kapt("com.google.dagger:hilt-android-compiler:2.38.1")

    // Hilt Jetpack Compose integration
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0-alpha03")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    implementation("com.google.accompanist:accompanist-insets:0.21.1-beta")

}
