val developerId: String by project
val releaseArtifact: String by project

plugins {
    alias(libs.plugins.git.publish)
}

gitPublish {
    repoUri.set("git@github.com:$developerId/$releaseArtifact.git")
    branch.set("gh-pages")
    contents.from("src")
}

tasks.register(LifecycleBasePlugin.CLEAN_TASK_NAME) {
    delete(layout.buildDirectory)
}
