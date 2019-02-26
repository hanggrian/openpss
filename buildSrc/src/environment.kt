const val DATABASE_USER = "OPENPSS_DATABASE_USER"
const val DATABASE_PASS = "OPENPSS_DATABASE_PASS"

fun env(key: String) = checkNotNull(System.getenv(key)) {
    "Entries required in system environment: $DATABASE_USER, $DATABASE_PASS."
}