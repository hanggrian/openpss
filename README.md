[![CircleCI](https://img.shields.io/circleci/build/gh/hanggrian/openpss)](https://app.circleci.com/pipelines/github/hanggrian/openpss/)
[![GitHub Releases](https://img.shields.io/github/release/hanggrian/openpss)](https://github.com/hanggrian/openpss/releases/)
[![OpenJDK](https://img.shields.io/badge/jdk-17%2B-informational)](https://openjdk.org/projects/jdk/17/)

# OpenPSS

![OpenPSS][logo]

Point of Sale software specifically designed for offset and digital printing business.
Powered by JavaFX and Kotlin frameworks.
Heavily under development.

## Features

- Multi-language: currently supports English and Bahasa.

## How to use

#### Download

Head to releases to download this app in 3 variants:

- `app` - Native MacOS app.
- `zip` - Compressed folder containing Windows 64-bit libraries and executables.
- `jar` - Smaller Java executable that requires JRE 1.8.

#### Database

[MongoDB] is required to run the app, install it on server system and grant
privileges to the main user in admin database:

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
		},
		{
			"role": "executeFunctions",
			"db": "admin"
		}
	]
}
```

Where `executeFunctions` is a custom role:

```json
{
	"role": "executeFunctions",
	"privileges": [
		{
			"resource": {
				"anyResource": true
			},
			"actions": [
				"anyAction"
			]
		}
	],
	"roles": [ ]
}
```

## Developer note

#### Tech stacks

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

#### How to build

To open the project, you're going to need Oracle JDK 1.8, IntelliJ IDEA and
optional SceneBuilder. Testing the app requires authenticated MongoDB server
over IP address (or localhost).

Then, simply follow steps below to build [releases]:

- Apply [ktlint] IDE settings (preferably in default-level, not project), if not already.
- Run `./gradlew clean build`, it will automatically perform additional tasks in the process.
  - Generate sources in `app/build/generated`.
  - Check Kotlin code style.
  - Ensure all test specs are successful.
- Use [shadowJar] and [packr] to build native packages from jar with following steps.

[logo]: /openpss/src/main/resources/image/logo.png
[MongoDB]: https://www.mongodb.com/
[ktlint]: https://github.com/pinterest/ktlint
[packr]: https://github.com/hanggrian/packaging-gradle-plugin
