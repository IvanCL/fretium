# AGENTS.md — AI context for Fretium (Android)

This file gives AI coding assistants (Claude, Copilot, Cursor, etc.) the context needed to work effectively on this codebase.

## Project purpose

Fretium Android is a native MVP that teaches electric guitar to Spanish-speaking learners. It's the Android port of the Fretium web app (Astro SSR) — chord/song data, progress model and audio behaviour are ported 1:1. Everything runs on-device: no backend, no network calls, no cloud sync.

## Tech stack

- **UI:** Jetpack Compose + Material 3
- **DI:** Hilt
- **Navigation:** Navigation Compose (bottom nav + nested `songs/{songId}` route)
- **Persistence:** Room (users, progress) + DataStore (active profile)
- **Profiles:** No auth — a local profile is just a name (`UserRepository.enterProfile`), created on first use or resumed if it already exists. No password, no server, nothing to protect remotely.
- **Audio playback:** On-device synthesis via `AudioTrack` (no audio assets)
- **Pitch detection:** `AudioRecord` + autocorrelation (ported from the web tuner)
- **Min/target SDK:** 26 / 35
- **Build:** AGP 8.5.2, Kotlin 2.0.21, KSP, Gradle version catalog (`gradle/libs.versions.toml`)

## Repository layout

```
app/src/main/java/com/fretwise/android/
  data/
    model/            # Chord, Song, Level, AppUser + static ChordsData/SongsData/AudioData
    local/             # Room entities/DAOs, AppDatabase, SessionPreferences (DataStore)
    UserRepository.kt      # enterProfile/logout, active-profile flow
    ProgressRepository.kt  # learned + practice_count upsert logic
    ChordRepository.kt / SongRepository.kt   # thin wrappers over static data
  audio/
    ChordAudioEngine.kt # chord synthesis + AudioTrack playback
    TunerEngine.kt       # AudioRecord capture → Flow<Double> frequency
    PitchDetector.kt     # autocorrelate() + freqToNote()
  di/
    DatabaseModule.kt   # Room providers
  ui/
    navigation/         # Screen routes, bottom nav, FretiumApp/NavHost
    auth/                # ProfileScreen + ProfileViewModel, SessionViewModel
    dashboard/           # Progress overview + level selector
    chords/              # Chord list + components/ChordDiagram.kt (Canvas)
    practice/            # Quiz mode ViewModel + screen
    songs/               # Song list + player (BPM, auto-advance) ViewModel + screen
    tuner/               # Tuner screen + ViewModel
    theme/               # Color/Theme/Type (Material3 dark palette matching the web app)
```

## Key conventions

- **No images or SVG assets for chord diagrams** — `ChordDiagram.kt` draws everything with Compose `Canvas` (strings, frets, barre, finger dots, X/O symbols). Proportions are ported from the web app's `chordSvg.ts` (110×140 design space, converted to fractions).
- **No backend** — every repository reads/writes Room or DataStore directly. Do not add Retrofit/Ktor or any network client for MVP features.
- **MVVM** — one `@HiltViewModel` per screen, injected repositories, `StateFlow<UiState>` exposed to Compose via `collectAsStateWithLifecycle()`.
- **Static chord/song data lives in Kotlin objects** (`ChordsData`, `SongsData`, `ChordFrequencies`, `TunerReference`) under `data/model/`, not in Room — they're read-only reference data, not user data.
- **Audio synthesis is 100% on-device**, no audio files. `ChordAudioEngine` mirrors the Web Audio graph from the reference app (per-string saw+triangle oscillators, strum offset, one-pole lowpass, attack/decay envelope, master exponential decay). The frequency map `ChordFrequencies.MAP` in `data/model/AudioData.kt` is the single source of truth — do not duplicate it elsewhere.
- **No comments** in source unless the WHY is non-obvious. Code is self-documenting via naming.
- All user-facing copy is in **Spanish**. Commit messages and code identifiers are in **English**.

## Database schema (Room)

```
users    (id, name UNIQUE COLLATE NOCASE, level, created_at)
progress (id, user_id FK CASCADE, chord_name, learned, practice_count, UNIQUE(user_id, chord_name))
```

There is no `sessions` table and no password column — the active profile is just a user id persisted in DataStore (`SessionPreferences`), since there's no server-side session/cookie model on-device and nothing to authenticate.

## Chord data model

Each `Chord` in `data/model/ChordsData.kt` has:
- `positions: List<Int>` — fret number per string from string 6 (low E) to string 1 (high e). `-1` = muted, `0` = open.
- `fingers: List<Int?>` — which fretting finger (1–4) or `null`.
- `startFret` — the fret at which the diagram starts (default 1).
- `barre` — optional `Barre(fret, fromString, toString)` for barre chords.
- `level` — `Level.BEGINNER | INTERMEDIATE | ADVANCED`.

## Audio synthesis

`ChordAudioEngine.synthesize()` renders a full PCM buffer per chord (two oscillators per string — sawtooth + triangle — through a one-pole lowpass filter, 14ms strum offset per string, short attack/decay envelope, exponential master decay over ~2.2s) and plays it with `AudioTrack` in `MODE_STATIC`. Song mode calls `playChord()` once per beat; practice mode calls it on-demand via the "Escuchar" button.

When adding a new chord:
1. Add its entry to `ChordsData.ALL` in `data/model/ChordsData.kt`.
2. Add its frequencies to `ChordFrequencies.MAP` in `data/model/AudioData.kt`.
3. Run `./gradlew :app:assembleDebug` to verify.

## What not to do

- Do not add a network client, remote API, or cloud sync — this MVP is fully offline by design.
- Do not add chord diagram images/SVG/PNG assets — keep the Canvas-based renderer.
- Do not add email, OAuth, passwords or third-party auth — a local profile is just a name, nothing to authenticate.
- Do not add in-app purchases, notifications or social/multiplayer features.
- Do not use copyrighted song titles or progressions in `SongsData.kt`.
- Do not commit `local.properties`, `*.keystore`, or `app/build/` — all covered by `.gitignore`.
