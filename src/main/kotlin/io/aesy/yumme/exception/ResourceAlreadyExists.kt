package io.aesy.yumme.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT, reason = "Resource already exists")
class ResourceAlreadyExists: YummeException()
