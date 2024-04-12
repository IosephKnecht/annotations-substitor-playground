package io.github.iosephknecht.parameterized.list

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AwareParametrization(val value: Int)
