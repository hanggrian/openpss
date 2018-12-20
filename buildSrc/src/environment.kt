fun envUser() = find(KEY_USER)
fun envPass() = find(KEY_PASS)

private const val KEY_USER = "OPENPSS_USER"
private const val KEY_PASS = "OPENPSS_PASS"

private fun find(key: String) = checkNotNull(System.getenv(key)) {
    "Entries required in system environment: $KEY_USER and $KEY_PASS."
}