package io.aesy.yumme.controller

import io.aesy.yumme.dto.ErrorDto
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(
    "\${server.error.path:\${error.path:/error}}",
    consumes = [MediaType.ALL_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class ErrorController: ErrorController {
    @Hidden
    @RequestMapping
    fun onError(request: HttpServletRequest): ErrorDto {
        val status: HttpStatus = getStatus(request)

        return ErrorDto(
            Date(),
            status.value(),
            status.reasonPhrase,
            "",
            listOf(status.reasonPhrase)
        )
    }

    private fun getStatus(request: HttpServletRequest): HttpStatus {
        val statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) as Int?
            ?: return HttpStatus.INTERNAL_SERVER_ERROR

        return try {
            HttpStatus.valueOf(statusCode)
        } catch (ex: IllegalArgumentException) {
            HttpStatus.INTERNAL_SERVER_ERROR
        }
    }
}
