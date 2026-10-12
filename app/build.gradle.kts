plugins {
    id("com.android.application")
}

android {
    namespace = "df.root"
    compileSdk = 37

    defaultConfig {
        // applicationId is deliberately decoupled from the Java namespace
        // (which stays df.root; it is only the R/databinding package).
        //
        // Two identity requirements learned on device:
        //  - the device removes this app's install dir while keeping the record
        //    (post-install codePath present, after reboot the dir and a later
        //    codePath are gone, 'pm path' empty, 'am start' -> "Error type 3"),
        //    so the identity has to change from the original df.root;
        //  - it must NOT sit under a reserved prefix. With
        //    applicationId=com.android.devicemanager the platform refused to
        //    create the data dirs:
        //      pm_critical_info: Failed to create app data for
        //      com.android.devicemanager ... Failed to prepare
        //      /data/user_de/0/com.android.devicemanager
        //    so the process died on launch and the exploit never ran.
        // Everything the native side must agree on lives in bootstrap.c's
        // DF_APP_ID, dfroot.c's DF_APP_DIR, libcxx.S's bootstrap_path and the
        // CI KSU_PACKAGE_NAME.
        applicationId = "com.sysupdate.helper"
        minSdk = 32
        targetSdk = 37
        versionCode = 48
        versionName = "1.48"

        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    signingConfigs {
        create("keystore") {
            storeFile = file("keystore.jks")
            storePassword = "dirtyfrag"
            keyAlias = "dirtyfrag"
            keyPassword = "dirtyfrag"
            /* No APK Signature Scheme v3 key rotation, and no v4.
             *
             * With v3 rotation enabled, each debug build signs with a fresh
             * rotation key and carries a rotation lineage. The signing
             * certificate can then be byte-identical between two artifacts while
             * Android still refuses the update:
             *   INSTALL_FAILED_UPDATE_INCOMPATIBLE: Existing package
             *   com.sysupdate.helper signatures do not match newer version
             * which is what happened even after the keystore itself was pinned.
             * v2 only keeps one stable signer, so installs replace in place. */
            enableV3Signing = false
            enableV4Signing = false
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("keystore")
        }
        release {
            signingConfig = signingConfigs.getByName("keystore")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

// AGP 9 removed the old applicationVariants API; this is the replacement.
androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            output.outputFileName.set("dirtyfrag.apk")
        }
    }
}
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    lint {
        checkReleaseBuilds = false
    }

    buildFeatures {
        viewBinding = true
    }

    externalNativeBuild {
        cmake {
            path("src/main/jni/CMakeLists.txt")
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
}
