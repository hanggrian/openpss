val releaseGroup: String by project
val releaseVersion: String by project

allprojects {
    group = releaseGroup
    version = releaseVersion
}

tasks.register(LifecycleBasePlugin.CLEAN_TASK_NAME) {
    delete(layout.buildDirectory)
}
