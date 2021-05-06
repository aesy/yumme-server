package io.aesy.yumme.util

import io.aesy.yumme.exception.YummeException

class NoSuchResourceException(path: String): YummeException("Resource not found: '$path'")
