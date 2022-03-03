package io.aesy.test.util

import io.aesy.yumme.exception.YummeException

class NoSuchResourceException(path: String): YummeException("Resource not found: '$path'")
