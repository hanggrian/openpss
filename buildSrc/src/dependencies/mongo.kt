private const val VERSION_MONGODB = "3.9.1"

fun Dependencies.mongo(module: String) =
    "org.mongodb:mongo-$module:$VERSION_MONGODB"