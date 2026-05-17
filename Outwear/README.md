# Outwear 🧥

An Android outerwear clothing review app built with Jetpack Compose, MVVM, Room, Hilt, and Coroutines.

## Tech Stack
- **UI**: Jetpack Compose + Material3
- **Architecture**: MVVM (ViewModel + StateFlow + Repository)
- **Database**: Room (5 entities, full CRUD)
- **DI**: Dagger Hilt
- **Async**: Kotlin Coroutines + Flow
- **Navigation**: Navigation Compose
- **Images**: Coil

## Screens
| Screen | Description |
|--------|-------------|
| Home | Featured items, top-rated row, recent reviews |
| Discover | Search + category filter with LazyColumn list |
| Product Detail | Full item info, rating breakdown, reviews preview |
| Reviews | All reviews with sort & star-filter |
| Add Review | Full validated review form with sub-ratings |
| Profile | User stats, wishlist, review history |

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 11+
- Android SDK 35

### Setup

1. **Clone the repo**
   ```bash
   git clone https://github.com/YOUR_USERNAME/Outwear.git
   cd Outwear
   ```

2. **Get the Gradle Wrapper** (first time only — choose one):
   ```bash
   # Option A: run the bootstrap script (requires internet)
   chmod +x bootstrap.sh && ./bootstrap.sh

   # Option B: if you have Gradle installed locally
   gradle wrapper --gradle-version=8.6

   # Option C: copy gradle-wrapper.jar from any existing Android project
   cp /path/to/other/project/gradle/wrapper/gradle-wrapper.jar gradle/wrapper/
   ```

3. **Open in Android Studio**
   - File → Open → select the `Outwear` folder
   - Android Studio will detect the Gradle wrapper and sync automatically
   - Set your SDK path when prompted (or edit `local.properties`)

4. **Run**
   - Select a device/emulator and press ▶ Run

## Project Structure
```
app/src/main/java/com/outwear/app/
├── di/                          ← Hilt modules
├── model/
│   ├── local/
│   │   ├── dao/                 ← 5 Room DAOs
│   │   ├── db/                  ← OutwearDatabase
│   │   ├── entity/              ← 5 Room entities
│   │   └── util/                ← DatabaseSeeder
│   └── repository/
│       ├── mappers/             ← Domain models + mappers
│       ├── Repositories.kt      ← Interfaces
│       └── RepositoryImpls.kt   ← Implementations
├── presentation/
│   ├── navigation/              ← NavGraph + routes
│   ├── theme/                   ← Theme + Typography
│   ├── ui/
│   │   ├── components/          ← Shared reusable components
│   │   └── screens/
│   │       ├── home/
│   │       ├── discover/
│   │       ├── product/
│   │       ├── review/
│   │       ├── addreview/
│   │       └── profile/
│   └── viewmodel/               ← 6 ViewModels with UiState
├── MainActivity.kt
└── OutwearApplication.kt
```
