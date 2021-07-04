[![version](https://img.shields.io/github/release/hendraanggrian/openpss)](https://github.com/hendraanggrian/openpss/releases/latest)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081)](https://ktlint.github.io/)

OpenPSS
=======
![OpenPSS][logo]

Point of Sale software specifically designed for offset and digital printing business.
Powered by Kotlin frameworks, heavily under development.

Features
--------
 * Multi-platform, client available in Windows and macOS.
 * Multi-language, currently supports English and Bahasa.

How to use
----------

#### Download
Head to releases to download this app in 3 variants:
 * `dmg`: macOS images, mount and obtain the `app` file.
 * `exe`: Windows executables wrapped in 7-zip SFX installer.
 * `jar`: Single Java executable file that requires JRE 1.8.

#### Database
[MongoDB] is required to run the app,
install it on server system and grant privileges to the main user in admin database:

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

Developer note
--------------

#### Tech stacks
Built with Kotlin in mind, this app is fully written in Kotlin frameworks:
 * Kotlin for JVM and Android
 * Ktor web framework
 * Kotlin NoSQL preview
 * Kotlin Coroutines
 * Gradle Kotlin DSL

Others include:
 * MongoDB server.
 * Google's Guava, used mainly for its powerful multimap.
 * Joda-Time and SLF4J, brought by Kotlin NoSQL.
 * Apache POI and some of its commons libraries.
 * Personal libraries.

#### How to build
To open the project, you're going to need Oracle JDK 1.8, IntelliJ IDEA and optional SceneBuilder.
Testing the app requires authenticated MongoDB server over IP address (or localhost).

Then, simply follow steps below to build [releases]:
 * Apply [ktlint] IDE settings (preferably in default-level, not project), if not already.
 * Run `./gradlew clean build`, it will automatically perform additional tasks in the process.
   * Generate sources in `app/build/generated`.
   * Check Kotlin code style.
   * Ensure all test specs are successful.
 * Use [packr] to build native packages from jar.

[logo]: /openpss-client-javafx/res/image/logo.png
[MongoDB]: https://www.mongodb.com/
[ktlint]: https://github.com/shyiko/ktlint
[packr]: https://github.com/hendraanggrian/packr
