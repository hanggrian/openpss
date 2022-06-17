private const val VERSION_JODA_TIME = "2.10.5"
private const val VERSION_JODA_TIME_GSON_SERIALIZERS = "1.8.0"

fun Dependencies.jodaTime() = "joda-time:joda-time:$VERSION_JODA_TIME"

fun Dependencies.jodaTimeSerializers() =
    "com.fatboyindustrial.gson-jodatime-serialisers:gson-jodatime-serialisers:$VERSION_JODA_TIME_GSON_SERIALIZERS"
