# AGENTS.md — Simple Flat

Android app: fullscreen solid color + brightness. Generate flat frames for astrophotography calibration.

## Build & run

```bash
./gradlew assembleDebug           # build debug APK
./gradlew lint                    # run lint
```

## Architecture

```
FullscreenActivity (AppCompatActivity, viewBinding)
├── ShowSettingsFragment (ViewPager2 + TabLayout container)
│   ├── SettingsFragment    — RGB/brightness SeekBars
│   └── ProfileFragment     — CRUD profiles, JSON export/import
├── buses/                  — RxJava3 event bus (PublishSubject + BehaviorSubject)
│   ├── ConfigsBus          — R/G/B/brightness read/write channels
│   └── ProfilesBus         — CRUD + import channels
├── database/               — Room DB
│   ├── SimpleFlatDatabase  — singleton, DB version 2
│   ├── ConfigsModel        — key-value store (ckey + value)
│   ├── ConfigsDao          — insert(REPLACE), getByKey, getAll
│   ├── ConfigsManager      — subscribes to ConfigsBus write/read requests
│   ├── ProfilesModel       — id, name, r_value, g_value, b_value, brightness_value
│   ├── ProfilesDao         — standard CRUD
│   ├── ProfilesManager     — subscribes to ProfilesBus requests, DB ops on bg thread
│   └── ImportProfilesType  — wrapper: List<ProfilesModel> + remove_old flag
├── adapters/               — FragmentStateAdapter, Spinner adapter
└── common/                 — Gson ExclusionStrategy (skips "id" on serialize)
```

Key: Java 8, minSdk 23, targetSdk 36, viewBinding, multidex, RxJava3, Room, Gson.

## Data flow (RxJava bus pattern)

UI publishes to `ConfigsBus.writeXxxRequest()` → `ConfigsManager` listens, writes Room DB on bg thread, pushes result back on `ConfigsBus.onXxxUpdated()` → Activity/Fragment subscribes to updated values. Same pattern for `ProfilesBus`.

## DB gotchas

- **Migration 1→2**: swaps `g_value` ↔ `b_value` columns in `profiles` table. Bug fix from old version. If changing schema, add new migration, bump `@Database(version=...)`.
- Configs use `OnConflictStrategy.REPLACE` on insert ⇒ upsert by unique `ckey` index.
- Profile import sets `model.setId(null)` for each imported model — workaround so Room auto-generates new PKs.

## RxJava subscriptions

All `Disposable` objects must be collected in `subs` list and disposed in `onDestroy()` of each Fragment/Activity. Missing cleanup = memory leak.

## Profile export/import

- Export path: `Documents/SimpleFlat/profiles_exported_<timestamp>.json`
- Import reads `.json` files from same directory
- Gson excludes `id` field via `ProfilesExclusionStrategy`
- `GsonBuilder().setExclusionStrategies(...)` must be used for both export and import

## Release build

`minifyEnabled true` + `shrinkResources true`. ProGuard rules in `app/proguard-rules.pro` (currently empty — add keep rules if new classes get stripped).

## Testing

Tests are example stubs only. No real test suite.

```
./gradlew test                    # JVM unit tests (app/src/test/)
./gradlew connectedAndroidTest    # instrumented tests (app/src/androidTest/, needs device/emulator)
```

## Style notes

- `_` prefix for static fields (e.g. `_currentBrightness`, `_settingsFragmentVisibile`)
- Italian localization in `values-it/strings.xml`
- Avoid adding comments — project convention is code without comments