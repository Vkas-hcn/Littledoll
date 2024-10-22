plugins {
    id("com.android.library")
    id("checkstyle")

    id("kotlin-android")
}

android {
    compileSdk = 34

    // Also update runcoverity.sh
    ndkVersion = "25.1.8937393"

    defaultConfig {
        minSdk = 21
        targetSdk = 34
        externalNativeBuild {
            cmake {}
        }
    }

    externalNativeBuild {
        cmake {
            version = "3.10.2"
            path = File("${projectDir}/src/main/cpp/CMakeLists.txt")
        }
    }

    testOptions.unitTests.isIncludeAndroidResources = true

    externalNativeBuild {
        cmake {
            path = File("${projectDir}/src/main/cpp/CMakeLists.txt")
        }
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets", "build/ovpnassets")

        }
    }



    lint {
        enable += setOf("BackButton", "EasterEgg", "StopShip", "IconExpectedSize", "GradleDynamicVersion", "NewerVersionAvailable")
        checkOnly += setOf("ImpliedQuantity", "MissingQuantity")
        disable += setOf("MissingTranslation", "UnsafeNativeCodeLocation")
    }


    flavorDimensions += listOf("implementation")

    productFlavors {
        create("skeleton") {
            dimension = "implementation"
            buildConfigField("boolean", "openvpn3", "false")
        }
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
            isUniversalApk = true
        }
    }

    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
        }
    }


}

var swigcmd = "swig"
// Workaround for macOS(arm64) and macOS(intel) since it otherwise does not find swig and
// I cannot get the Exec task to respect the PATH environment :(
if (file("/opt/homebrew/bin/swig").exists()) swigcmd = "/opt/homebrew/bin/swig"
else if (file("/usr/local/bin/swig").exists()) swigcmd = "/usr/local/bin/swig"


fun registerGenTask(variantName: String, variantDirName: String): File {
    val baseDir = File(buildDir, "generated/source/ovpn3swig/${variantDirName}")
    val genDir = File(baseDir, "net/openvpn/ovpn3")

    tasks.register<Exec>("generateOpenVPN3Swig${variantName}") {

        doFirst {
            mkdir(genDir)
        }
        commandLine(
            listOf(
                swigcmd,
                "-outdir",
                genDir,
                "-outcurrentdir",
                "-c++",
                "-java",
                "-package",
                "net.openvpn.ovpn3",
                "-Isrc/main/cpp/openvpn3/client",
                "-Isrc/main/cpp/openvpn3/",
                "-DOPENVPN_PLATFORM_ANDROID",
                "-o",
                "${genDir}/ovpncli_wrap.cxx",
                "-oh",
                "${genDir}/ovpncli_wrap.h",
                "src/main/cpp/openvpn3/client/ovpncli.i"
            )
        )
        inputs.files("src/main/cpp/openvpn3/client/ovpncli.i")
        outputs.dir(genDir)

    }
    return baseDir
}


dependencies {
    // https://maven.google.com/web/index.html
    // https://developer.android.com/jetpack/androidx/releases/core
    implementation("androidx.annotation:annotation:1.3.0")
    implementation("androidx.core:core:1.9.0")

    testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.21")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:3.9.0")
    testImplementation("org.robolectric:robolectric:4.5.1")
    testImplementation("androidx.test:core:1.4.0")
}
