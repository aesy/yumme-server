package io.aesy.yumme.logging

import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class RequestLogFilter: OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        try {
            chain.doFilter(request, response)
        } finally {
            logger.debug(createMessage(request, response))
        }
    }

    private fun createMessage(request: HttpServletRequest, response: HttpServletResponse): String {
        val query = request.queryString
        val client = request.remoteAddr
        val auth = request.remoteUser
        val session = request.getSession(false)
        val message = StringBuilder()
            .append(request.method)
            .append(' ')
            .append(request.requestURI)

        if (query != null) {
            message.append('?').append(query)
        }

        message.append(", response=").append(response.status)

        if (StringUtils.hasLength(client)) {
            message.append(", client=").append(client)
        }

        if (auth != null) {
            message.append(", auth=").append(auth)
        }

        if (session != null) {
            message.append(", session=").append(session.id)
        }

        return message.toString()
    }
}
