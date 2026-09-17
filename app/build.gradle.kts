plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "com.catcorp.catquest"
  compileSdk {
    version = release(36) {
      minorApiLevel = 1
    }
  }

  defaultConfig {
    applicationId = "com.catcorp.catquest"
    minSdk = 30
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
  }
  kotlinOptions {
    jvmTarget = "11"
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation(libs.core.ktx)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  // CameraX (captura simplificada)
  val cameraxVersion = "1.3.4"
  implementation("androidx.camera:camera-core:$cameraxVersion")
  implementation("androidx.camera:camera-camera2:$cameraxVersion")
  implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
  implementation("androidx.camera:camera-view:$cameraxVersion")

  // Coil (carregamento leve de imagens em Compose)
  implementation("io.coil-kt:coil-compose:2.6.0")

  // SQLite
  implementation("androidx.sqlite:sqlite-ktx:2.4.0")

  // Media3 ExoPlayer (para o áudio da retrospectiva)
  implementation("androidx.media3:media3-exoplayer:1.3.1")

  // WorkManager (agendamento da notificação mensal)
  implementation("androidx.work:work-runtime-ktx:2.9.0")

}
