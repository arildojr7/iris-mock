package dev.arildo.iris.mock

import dev.arildo.iris.mock.util.IRIS_MOCK_TAG
import java.util.logging.Logger

object IrisMock {
    internal var enableLogs = false
    internal val logger = Logger.getLogger(IRIS_MOCK_TAG)
}
