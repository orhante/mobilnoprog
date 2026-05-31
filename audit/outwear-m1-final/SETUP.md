# Outwear — Final Project Setup Guide

## What was added

### 1. Network Layer (Retrofit)

| File | Purpose |
|------|---------|
| `model/data/remote/dto/ProductDto.kt` | DTOs mapping FakeStore API JSON → Kotlin data classes |
| `model/data/remote/api/FakeStoreApiService.kt` | Retrofit interface with GET, POST, PUT, DELETE |
| `model/di/NetworkModule.kt` | Hilt `@Module` providing OkHttpClient, Retrofit, and the service |
| `model/repository/remote/ProductNetworkRepository.kt` | Repository wrapping every API call in `NetworkResult<T>` |
| `presentation/viewmodel/NetworkProductsViewModel.kt` | ViewModel exposing `uiState` + `actionState` flows |
| `presentation/ui/screens/auth/NetworkProductsScreen.kt` | Screen listing API products with Create / Update / Delete demo |

**API used:** [https://fakestoreapi.com](https://fakestoreapi.com) — free, no key needed.

---

### 2. Firebase Integration (Auth + Firestore)

| File | Purpose |
|------|---------|
| `model/di/FirebaseModule.kt` | Hilt `@Module` providing `FirebaseAuth` and `FirebaseFirestore` |
| `model/repository/remote/AuthRepository.kt` | Sign-up, sign-in, sign-out, `authStateFlow()` for persistent session |
| `model/repository/remote/FirestoreWishlistRepository.kt` | Realtime wishlist sync via Firestore `wishlists` collection |
| `presentation/viewmodel/AuthViewModel.kt` | Auth state machine (`Idle → Loading → Authenticated / Error`) |
| `presentation/ui/screens/auth/AuthScreen.kt` | Sign In / Sign Up screen with validation and error UI |

**Firestore collection:** `wishlists/{uid}` → `{ itemIds: [String], updatedAt: Long }`

---

## Firebase Setup (required before running)

1. Go to [https://console.firebase.google.com](https://console.firebase.google.com) and create a project.
2. Add an **Android app** with package name `com.outwear.app`.
3. Download `google-services.json` and replace `app/google-services.json`.
4. In Firebase console → **Authentication** → Enable **Email/Password** provider.
5. In Firebase console → **Firestore Database** → Create database (start in test mode for development).

---

## Architecture overview

```
UI (Compose Screens)
    │
    ▼
ViewModels (Hilt @HiltViewModel)
    │              │
    ▼              ▼
Remote Repos   Local Repos (Room)
    │
    ├── ProductNetworkRepository  →  Retrofit → fakestoreapi.com
    ├── AuthRepository            →  FirebaseAuth
    └── FirestoreWishlistRepository → Firestore (realtime)
```

---

## Running the app

```bash
# Make sure you have replaced google-services.json first
./gradlew assembleDebug
```

The app will open on the **Sign In** screen. Create an account, then navigate to:
- **Home / Discover / Profile** — existing local Room data
- **API tab** — live data from FakeStore REST API (GET/POST/PUT/DELETE demo)
- **Logout** button in bottom nav — session ends, redirected back to Sign In
