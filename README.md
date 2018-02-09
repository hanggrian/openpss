Wijaya Printing
===============
JavaFX app to manage commercial offset printing company.
Under development (POC).

Download
--------
Head to [releases] to download this app in 3 variants:
 * `app` - Native Mac OSX app.
 * `zip` - Compressed folder containing Windows 64-bit libraries and executables.
 * `jar` - Smaller Java executable that requires JRE 1.8.

There is also `scene.jar`, third-party library for SceneBuilder to design fxml files in this project.

Developer note
--------------
#### Tech stacks
Built with Kotlin in mind, this app is fully written in Kotlin and using some Kotlin-based frameworks:
 * Kotlin for JVM
 * Preview version of Kotlin NoSQL Framework
 * Experimental Kotlin Coroutines
 * Gradle Kotlin DSL
 * Spek Test Framework
 
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
 * Run `./gradlew shadowJar` to build single fat jar in `release`.
 * Use [packr] to build native packages from that jar with following steps.
   * Configure packr json files for each incremental version or JDK update. 
   * Execute `java -jar packr.jar packr-mac.json` to pack `app`. 
   * Execute `java -jar packr.jar packr-windows.json` to pack `zip`.

License
-------
    Copyright 2017 Hendra Anggrian

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
[releases]: https://github.com/hendraanggrian/wijayaprinting/releases
[ktlint]: https://github.com/shyiko/ktlint
[packr]: https://github.com/libgdx/packr
