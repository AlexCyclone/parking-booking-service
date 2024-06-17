package icu.cyclone.parking.booking.testcontainers

import org.testcontainers.containers.PostgreSQLContainer

class ForEachPostgresqlContainer private constructor() : PostgreSQLContainer<ForEachPostgresqlContainer?>(IMAGE_VERSION) {

    override fun stop() {
        // do nothing, JVM handles shut down
    }

    companion object {
        private const val IMAGE_VERSION = "postgres:16.3-alpine"
        private var container: ForEachPostgresqlContainer? = null

        val instance: ForEachPostgresqlContainer
            get() {
                if (container == null) {
                    container = ForEachPostgresqlContainer()
                }
                return container!!
            }
    }
}
