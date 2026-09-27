# MealQuest — KMP Home Meal Picker for iPad & Desktop

**MealQuest** is an offline-first, kid-friendly home meal catalog and selection app built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**, targeting **iPad (iPadOS)** as its primary experience alongside desktop/Android parity.

Designed for parents to rapidly catalog home-cooked recipes and for kids to visually pick daily meals without mealtime indecision.

---

## 🌟 Key Features

### 1. Visual Meal Catalog & Adaptive Tablet Grid
- **Responsive Tablet Grid**: Dynamic 16:9 meal cards automatically adapting to iPad portrait (3-column) and landscape (4/5-column) orientations (`GridCells.Adaptive(minSize = 220.dp)`).
- **High-DPI Photography & Emoji Fallbacks**: Offline-first meal imagery rendered via Coil 3 with appetizing fallback plates.
- **Visual Nutritional & Practical Badges**:
  - Nutrition Score (Super Healthy, Everyday, Occasional Treat)
  - School Lunch Suitability (Lunchbox Ready, Needs Thermos, Not Suitable)
  - Prep/Cooking time pill & dynamic star rating display
  - Relative "Last served" recency indicators (e.g., "Today", "2 days ago", "Never served").

### 2. Multi-Attribute Filtering Engine
- **Search**: Substring match across dish titles AND individual ingredients.
- **Meal Type**: Quick sticky header chips (All, Breakfast, Lunch, Dinner, Snack, Dessert) with live recipe counts.
- **Filter Sheet**:
  - Prep/Cooking time slider (5–90 min)
  - Effort Level chips (Quick & Easy, Moderate, Weekend Project)
  - School Lunch Suitability filter
  - Nutrition Score & Highlights (High-Protein, Low-Sugar, Veggie-Packed, Fiber-Rich)
  - Recency controls: "Never served" toggle and "Not eaten in at least X days" (3, 5, 7, 14 days)
  - Custom tag selection with live active filter counter.

### 3. "I Want This!" Kid-Centric Selection & History Tracking
- Prominent kid-accessible CTA button on dish details sheet.
- **Celebration Overlay**: Dynamic confetti particle animation and celebratory pop-up card.
- **Native Haptics**: iOS `UINotificationFeedbackGenerator(type: .success)` trigger on dish selection.
- **Instant History Logging**: Appends epoch timestamp to Room DB and updates "Last served" recency badge in real-time.

### 4. Dish Creation, Editing & Duplicate Flow
- Comprehensive dish creation/edit sheet with real-time validation.
- **Media Intake**: Native iOS camera / photo picker or desktop file dialog with automatic 1200px max downscaling and JPEG compression.
- **Ingredient Chip Generator**: Fast entry with comma/return tokenization and instant chip removal.
- **Collapsible Macro Nutrition**: Detailed calories, protein, carbs, fats, and fiber tracking.

---

## 🏗 Architecture & Tech Stack

| Layer | Technology |
|---|---|
| **Core Framework** | Kotlin Multiplatform (KMP 2.1.10) |
| **UI System** | Compose Multiplatform (1.7.3) + Material Design 3 |
| **Relational Database** | Room KMP (2.7.1) with Bundled SQLite (2.5.1) |
| **File / Media Storage** | Okio (3.10.2) in sandboxed Documents (`meals/{uuid}.jpg`) |
| **Image Loading** | Coil 3 (3.0.4) Compose Multiplatform |
| **Serialization** | `kotlinx.serialization` (1.7.3) JSON TypeConverters |
| **Date / Time** | `kotlinx-datetime` (0.5.0) |
| **Platform Target** | iPadOS (SwiftUI + UIKit embedding `ComposeUIViewController`), Desktop JVM |

---

## 🚀 Running the Project

### Prerequisites
- macOS with Xcode 15+ / 16+
- JDK 17+ or 21+ (e.g. Amazon Corretto 22)
  ```bash
  export JAVA_HOME="/Users/xiaojuangao/Library/Java/JavaVirtualMachines/corretto-22.0.2/Contents/Home"
  ```

### 1. Run on iPad Simulator
```bash
# 1. Build ComposeApp debug XCFramework
./gradlew :composeApp:assembleComposeAppDebugXCFramework

# 2. Build and launch Xcode project on iPad simulator
xcodebuild -project ipad-melon-meal-picker.xcodeproj \
  -scheme ipad-melon-meal-picker \
  -destination 'platform=iOS Simulator,name=iPad Pro 11-inch (M4)' \
  -derivedDataPath build/derivedData \
  build

# 3. Install and run via simctl
xcrun simctl install booted build/derivedData/Build/Products/Debug-iphonesimulator/ipad-melon-meal-picker.app
xcrun simctl launch booted com.melonapp.ipad-melon-meal-picker
```

### 2. Run Desktop App
```bash
./gradlew :composeApp:run
```

### 3. Run Unit Tests
```bash
./gradlew :composeApp:desktopTest
```

---

## 🧪 Automated Testing
Comprehensive test suite located in `composeApp/src/commonTest/kotlin/com/melon/mealpicker/MealFilterTest.kt` verifying:
- Search substring filtering (name and ingredient matching)
- MealType OR logic
- Tag AND logic
- Cooking time threshold boundaries
- School lunch suitability & nutrition criteria
- History recency calculations (`neverServedOnly`, `notEatenInDays`)
- Room serialization converters (`List<String>`, `Set<MealType>`, `NutritionInfo`)
