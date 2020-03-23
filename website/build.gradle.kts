plugins {
    `git-publish`
}

gitPublish {
    repoUri.set(RELEASE_WEBSITE)
    branch.set("gh-pages")
    contents.from("src")
}