package io.aesy.food.conversion

//val log: Logger = LoggerFactory.getLogger(EntityResponseBodyBanAdvice::class.java)
//
//@ControllerAdvice
//@Order(Int.MAX_VALUE)
//class EntityResponseBodyBanAdvice(
//    private val modelMapper: ModelMapper
//): AbstractMappingJacksonResponseBodyAdvice() {
//    override fun beforeBodyWriteInternal(
//        bodyContainer: MappingJacksonValue,
//        contentType: MediaType,
//        returnType: MethodParameter,
//        request: ServerHttpRequest,
//        response: ServerHttpResponse
//    ) {
//        val value = bodyContainer.value
//
//        val hasEntity = when (value) {
//            is Page<*> -> value.any { it::class.java.isAnnotationPresent(Entity::class.java) }
//            is Collection<*> -> value.stream().anyMatch { it!!::class.java.isAnnotationPresent(Entity::class.java) }
//            else -> bodyContainer.value::class.java.isAnnotationPresent(Entity::class.java)
//        }
//
//        if (!hasEntity) {
//            return
//        }
//
//        log.error("A response containing a @Entity object detected. " +
//                  "The response has been set to status 500. " +
//                  "Prefer a custom DTO object.")
//        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)
//        bodyContainer.value = Unit
//    }
//}
