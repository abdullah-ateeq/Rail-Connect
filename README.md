# RailConnect 🚂

**RailConnect** is a captivating railway puzzle game built natively for Android using **Jetpack Compose** and **Material 3**. Connect distant train terminals across a 6x6 grid by strategic placement and rotation of track tiles drawn from a limited supply bag.

---

## 🌟 Features

- **Dynamic Railway Canvas**: Custom Compose Canvas rendering for high-performance track tiles (Straight, Curve, T-Junction, Cross), stations, blocked terrain, and connected network highlights.
- **BFS Network Engine**: Real-time Breadth-First Search (BFS) graph traversal algorithm verifying track alignment and active network connectivity between terminals.
- **Synthesized Audio Engine**: Built-in dynamic synthesizer utilizing Android's `AudioTrack` API to produce custom retro sound effects (tile selection, placement, rotation, station connection, network completion, and failure) with zero audio file assets.
- **Interactive Gameplay HUD**:
  - Live game timer and move counter.
  - Station connection tracker (`X / 4`).
  - Tile Tray (3 active slots) with single-tap rotation and selection controls.
  - Track supply bag counter previewing incoming tiles.
  - Full move history with **Undo** capability and board restart.
- **Customization & Themes**:
  - App Themes: System, Light, and Dark mode.
  - Board Themes: **Classic** and **Midnight** canvas styling.
- **Persistence & Statistics**: Built with **Jetpack DataStore Preferences** to save user settings and track lifetime statistics (Games Played, Networks Completed, Best Moves, Best Time, Most Tracks Remaining).
- **Haptic & Visual Feedback**: Touch feedback, connection animations, glow overlays, and victory/defeat screens.

---

## 🎮 How to Play

1. **Objective**: Connect all **4 Train Terminals** (North Terminal, East Terminal, West Terminal, Central Depot) to complete the railway network.
2. **Select & Rotate**: Choose a track tile from your 3-slot Tile Tray and tap the rotate button to orient it correctly.
3. **Place Track**: Tap any open grid tile on the 6x6 board to place your track.
4. **Connection Rule**: Two adjacent cells connect only if both track ends align seamlessly towards each other.
5. **Manage Supply**: You have a total supply of **18 track tiles**. Plan your routes wisely before running out of tracks!

---

## 🛠 Tech Stack & Architecture

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel) + Single Activity Architecture
- **Navigation**: Navigation Compose
- **State Management**: Kotlin StateFlow & SharedFlow
- **Storage**: Jetpack DataStore Preferences
- **Asynchronous Execution**: Kotlin Coroutines
- **Graphics**: Custom Compose Canvas (`DrawScope`)
- **Audio**: Dynamic Synthesizer via `AudioTrack` API
- **Build System**: Gradle Version Catalog (`libs.versions.toml`)
- **Target SDK**: Android 15 / SDK 37 (Min SDK 24)

---

## 📂 Project Structure

```
com.example.railconnect/
├── audio/          # Dynamic AudioTrack synth sound manager
├── data/           # DataStore preferences & game settings model
├── engine/         # Graph traversal (BFS) railway network engine
├── model/          # Game state, puzzle configuration, grid positions, tiles
├── ui/
│   ├── components/ # Canvas board renderer, HUD elements, tile previews
│   ├── navigation/ # Rail NavHost & Screen route definitions
│   ├── screens/    # Splash, Home, Game, Result, Settings, How to Play, About
│   └── theme/      # Custom Material 3 color palettes, typography, shapes
└── viewmodel/      # GameViewModel & SettingsViewModel managing UI state
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug or newer
- JDK 11 or higher
- Android SDK 37 (Build Tools)

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/Rail-Connect.git
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle and run on an Android Emulator or physical device (Android 7.0 / SDK 24+).

---

## 📜 License

This project is open source and available under the [MIT License](LICENSE).
