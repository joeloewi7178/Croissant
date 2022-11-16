# Croissant

An android app that can attend HoYoLAB check-in events automatically

<https://play.google.com/store/apps/details?id=com.joeloewi.croissant>

# Stacks

- Room (SQLite Database)
- Hilt
- Compose
- Material Design 3 (Material You)
- WorkManager
- Datastore (Protobuf)
- App Startup
- Baseline Profile (Generated while CI/CD)

# Architectures

- Clean Architecture
- MVVM

# Before Build & Run

This project uses firebase and not contains *google-services.json* file because it's a server key.
There will be errors if you only cloned and run it, so you have to get *google-services.json* from
your own firebase project and put in to cloned project directory.
