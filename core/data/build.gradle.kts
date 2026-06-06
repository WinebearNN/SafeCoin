import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

fun loadAiConfigProperties(): Properties {
    val properties = Properties()
    val configFile = rootProject.file("ai_config.properties")
    if (configFile.exists()) {
        configFile.inputStream().use { properties.load(it) }
    }
    return properties
}

android {
    namespace = "com.safecoin.safecoin.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 28

        val aiConfig = loadAiConfigProperties()
        buildConfigField("String", "YANDEX_API_KEY", "\"${aiConfig.getProperty("yandex.api.key", "")}\"")
        buildConfigField("String", "YANDEX_IAM_TOKEN", "\"${aiConfig.getProperty("yandex.iam.token", "")}\"")
        buildConfigField("boolean", "YANDEX_USE_IAM", "${aiConfig.getProperty("yandex.use.iam", "false")}")
        buildConfigField("String", "YANDEX_FOLDER_ID", "\"${aiConfig.getProperty("yandex.folder.id", "")}\"")
        buildConfigField("String", "YANDEX_MODEL", "\"${aiConfig.getProperty("yandex.model", "yandexgpt/latest")}\"")
        buildConfigField("double", "YANDEX_TEMPERATURE", aiConfig.getProperty("yandex.temperature", "0.3"))
        buildConfigField("String", "YANDEX_MAX_TOKENS", "\"${aiConfig.getProperty("yandex.max.tokens", "2000")}\"")
        buildConfigField("boolean", "YANDEX_STREAM", "${aiConfig.getProperty("yandex.stream", "true")}")
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
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.okhttp)
}
