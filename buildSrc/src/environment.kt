const val DATABASE_USER = "OPENPSS_DATABASE_USER"
const val DATABASE_PASS = "OPENPSS_DATABASE_PASS"
const val SERVER_HOST = "OPENPSS_SERVER_HOST"
const val SERVER_PORT = "OPENPSS_SERVER_PORT"

fun env(key: String) = checkNotNull(System.getenv(key)) {
    "Entries required in system environment: $DATABASE_USER, $DATABASE_PASS, $SERVER_HOST, and $SERVER_PORT."
}