# Pancreatic Cancer Android App (Kotlin)

Android client for the pancreatic cancer prediction API. It provides a **schema-driven patient input form** for inference and a **model evaluation dashboard** (metrics + plots) to help users understand performance, with special attention to false negatives.

## Repository links

- Android app: [Android-Pancreatic-Cancer](https://github.com/Sami-dev07/Android-Pancreatic-Cancer-Detection)
- Backend API: [Pancreatic-Cancer-Detection](https://github.com/Sami-dev07/Pancreatic-Cancer-Detection)

This app includes:

- Custom patient prediction flow (schema-driven form)
- Model performance dashboard (cards/chips/plots)
- Separate model metrics screen (accuracy, precision, sensitivity, specificity, F1, ROC-AUC, FNR)
- Full-screen plot preview

---

## Architecture

MVVM-style structure:

- `api/` — Retrofit interfaces and response models
- `data/` — repository (`ModelRepository`)
- `viewmodel/` — screen state + business logic
- Activities/Adapters — rendering and interactions

---

## Requirements

- Android Studio (latest stable)
- JDK 17
- Android SDK (min SDK 24)
- Running backend API

---

## Configure API URL

Set backend base URL in:

- `app/build.gradle.kts`

```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://PC Ip:8000\"")
```

For emulator, use `http://10.0.2.2:8000`.

For physical phone, use your PC LAN IP and ensure same Wi-Fi + firewall rule for port 8000.

---

## Run the App

1. Open `android-app/` in Android Studio  
2. Sync Gradle  
3. Build/Run on device or emulator

---

## Screens

### Home (`MainActivity`)

- Open custom prediction flow
- Open model performance dashboard
- Open dedicated metrics screen

### Custom Patient Prediction (`FeaturesActivity`)

- Fetches schema from `GET /prediction/schema`
- Builds dynamic inputs from backend metadata
- Validates and submits to `POST /predict`
- Displays prediction + confidence + safety note

### Model Performance Dashboard (`PlotsActivity`)

- Fetches compact blocks from `GET /model/performance/blocks`
- Shows summary chips and metric cards
- Shows confusion matrix card
- Displays plot grid
- Tap plot to open full-screen

### Model Metrics (`MetricsActivity`)

- Fetches `GET /model/summary`
- Shows ACC/F1/PRE/SEN/SPEC/AUC/FNR chips and detailed values

### Plot Detail (`PlotDetailActivity`)

- Full-screen image rendering using Coil

---

## API Dependencies

Primary endpoints used:

- `GET /prediction/schema`
- `POST /predict`
- `GET /model/performance/blocks`
- `GET /model/summary`
- plot images via `GET /static/plots/{filename:path}`

---

## Clinical interpretation (why FN matters)

In cancer-risk prediction, **false negatives** (missed cancers) are particularly important. The app highlights:

- **Sensitivity** (Recall): how many cancers are detected
- **Specificity**: how many non-cancers are correctly ruled out
- **FNR**: missed-cancer rate (lower is better)
- **NPV**: when the model predicts “no cancer”, how often that’s correct

Always treat results as decision support only — **not a diagnosis**.




