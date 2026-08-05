plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.snaplyexample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.snaplyexample"
        // The SDK's own floor. Raising yours is fine; going below 24 is not.
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // The whole integration. Resolved from Maven Central with no credentials — nothing else to add,
    // and no transitive setup for the host app to do.
    implementation("com.snaplyagent:agent:2.22.1")
}
