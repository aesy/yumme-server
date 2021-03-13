package io.aesy.yumme

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableAsync
@EnableAspectJAutoProxy
@EnableCaching
@EnableScheduling
@EnableJpaRepositories
@EnableTransactionManagement(proxyTargetClass = true)
@SpringBootApplication
class YummeApplication

fun main(args: Array<String>) {
    runApplication<YummeApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
