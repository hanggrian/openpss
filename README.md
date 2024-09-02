[![CircleCI](https://img.shields.io/circleci/build/gh/hanggrian/openpss)](https://app.circleci.com/pipelines/github/hanggrian/openpss/)
[![GitHub Releases](https://img.shields.io/github/release/hanggrian/openpss)](https://github.com/hanggrian/openpss/releases/)
[![Java](https://img.shields.io/badge/java-8+-informational)](https://docs.oracle.com/javase/8/)

# OpenPSS

![The OpenPSS logo.](https://github.com/hanggrian/openpss/raw/assets/OpenPSS.png)

Point of Sale software specifically designed for offset and digital printing
business. Powered by JavaFX and Kotlin frameworks. Heavily under development.

- Multi-language: currently supports English and Bahasa.
- Priviledged-based employee management, certain actions can only be done by
  admins.

## How to use

### Download

Head to releases to download this app in several variants:

- `dmg` - macOS installer for Apple Silicon.
- `exe` - Windows 64-bit installer.

### Database

[MongoDB](https://www.mongodb.com/) is required to run the app, install it on
server system and grant privileges to the main user in admin database:

```json
{
  "user": "my_name",
  "pwd": "my_password",
  "roles": [
    {
      "role": "userAdminAnyDatabase",
      "db": "admin"
    },
    {
      "role": "dbAdminAnyDatabase",
      "db": "admin"
    },
    {
      "role": "readWriteAnyDatabase",
      "db": "admin"
    }
  ]
}
```

### Hardware

Type | Tested Device | Note
--- | --- | ---
ESC/POS compatible printer | Epson LX-310 | Printed in continuous form paper sized **4.25" &times; 5.5"** (quarter Letter).
Fingerprint scanner | E-Clocking Reader | Employee and attendee records are obtained with *Microsoft Excel* using plugin.

## Developer note

Built with Kotlin in mind, this app is fully written in Kotlin and using some
Kotlin-based frameworks:

- Kotlin for JVM
- Preview version of Kotlin NoSQL Framework
- Kotlin Coroutines
- Gradle Kotlin DSL

Others include:

- MongoDB server.
- Google's Guava, used mainly for its powerful multimap.
- Joda-Time and SLF4J, brought by Kotlin NoSQL.
- Apache POI and some of its commons libraries.
- Personal libraries.
