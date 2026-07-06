# 🎸 Fretium (Android)

Native Android app for learning electric guitar — chords, dynamic practice, song progressions and a chromatic tuner. Built with Kotlin + Jetpack Compose, 100% offline (no backend).

This is the Android counterpart of the [Fretium web app](https://github.com/) (Astro SSR). Business logic, chord/song data and audio behaviour are ported 1:1 from the web version.

## Features

| Feature | Description |
|---|---|
| **User accounts** | Register with a username and password — no email, no backend |
| **3 learning levels** | Beginner → Intermediate → Advanced, selectable from the dashboard |
| **Chord library** | 21 chords with fretboard diagrams drawn on Canvas (no images/SVG), fingering numbers and tips |
| **Practice mode** | Randomised chord quiz: mark chords as learned, hard or skip |
| **Song practice** | 11 royalty-free progressions that advance on screen at an adjustable BPM |
| **Chord audio** | On-device synthesis via `AudioTrack` — one strum per beat in song mode; on-demand in practice mode |
| **Chromatic tuner** | Microphone-based pitch detection via autocorrelation (`AudioRecord`), standard EADGBE reference |
| **Progress tracking** | Per-user learned/practice counts stored locally in Room |

## Quick start

Open the project in Android Studio (Koala or newer) and run the `app` module, or from the CLI:

```bash
cd fretium-android
./gradlew installDebug
```

Requires Android SDK platform 35 and a device/emulator with API 26+. No environment variables, no backend, no network access needed — everything runs on-device.

## Song library

All progressions are original practice sequences or traditional public-domain arrangements — no copyright restrictions.

| Level | Songs |
|---|---|
| Beginner | Campfire Road, Blues en Mi menor, Folk Waltz, Simple Rock |
| Intermediate | Autumn Minor, Blues Shuffle en La, Rock Anthem, Latin Groove |
| Advanced | Jazz Evening, Bossa Feel, Minor Seventh Walk |

## Screens

| Screen | Description |
|---|---|
| Login / Register | Local auth, no email |
| Dashboard | Progress overview, level selector, quick access |
| Chords | Full chord library with Canvas-drawn diagrams |
| Practice | Randomised chord quiz with on-demand audio |
| Songs | Song list + player with BPM slider and auto-advance |
| Tuner | Chromatic tuner with animated needle |

## Data persistence

Everything is stored on-device: user accounts and chord progress in a Room database (`fretium.db`), and the active session in DataStore. Uninstalling the app deletes all data — there is no cloud sync in this MVP.
