package com.halcyon.tinder.exceptioncore.handler

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.halcyon.tinder.exceptioncore.ApiException
import com.halcyon.tinder.exceptioncore.ErrorDetailsResponse
import com.halcyon.tinder.exceptioncore.ValidationErrorsResponse
import feign.FeignException
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ApiException::class)
    fun handleApiExceptions(e: ApiException): ResponseEntity<ErrorDetailsResponse> {
        val status = e.javaClass.getAnnotation(ResponseStatus::class.java).value
        val message = e.message ?: "Unknown error"

        val errorDetails = ErrorDetailsResponse(
            status = status.value(),
            error = e.javaClass.simpleName,
            message = message
        )

        return ResponseEntity(errorDetails, status)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupportedException(e: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorDetailsResponse> {
        val status = HttpStatus.METHOD_NOT_ALLOWED

        val errorDetails = ErrorDetailsResponse(
            status = status.value(),
            error = e.javaClass.simpleName,
            message = e.message ?: "Method not supported"
        )

        return ResponseEntity(errorDetails, status)
    }

    @ExceptionHandler(FeignException::class)
    fun handleFeignException(e: FeignException): ResponseEntity<ErrorDetailsResponse> {
        val status = HttpStatus.resolve(e.status()) ?: HttpStatus.INTERNAL_SERVER_ERROR
        val message = e.message ?: "Feign client error"

        val errorDetails = ErrorDetailsResponse(
            status = status.value(),
            error = e.javaClass.simpleName,
            message = message
        )

        return ResponseEntity(errorDetails, status)
    }

    @ExceptionHandler(
        MethodArgumentNotValidException::class,
        ConstraintViolationException::class,
        MethodArgumentTypeMismatchException::class,
        HttpMessageNotReadableException::class
    )
    fun handleValidationExceptions(ex: Exception): ResponseEntity<ValidationErrorsResponse> {
        val errors: List<Map<String, String>> = when (ex) {
            is MethodArgumentNotValidException -> getMethodArgumentNotValidErrors(ex)
            is ConstraintViolationException -> getConstraintViolationErrors(ex)
            is MethodArgumentTypeMismatchException -> getMethodArgumentTypeMismatchErrors(ex)
            is HttpMessageNotReadableException -> getJsonParseErrors(ex)
            else -> emptyList()
        }

        val validationErrorsResponse = ValidationErrorsResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            errors = errors
        )

        return ResponseEntity.badRequest().body(validationErrorsResponse)
    }

    private fun getMethodArgumentNotValidErrors(ex: MethodArgumentNotValidException) : List<Map<String, String>> {
        return ex.bindingResult.fieldErrors
            .map { error ->
                mapOf(
                    "field" to toSnakeCase(error.field),
                    "message" to (error.defaultMessage ?: "Unknown error")
                )
            }
            .sortedBy { it["field"] }
    }

    private fun toSnakeCase(original: String): String {
        return original.replace(Regex("([a-z])([A-Z]+)"), "$1_$2").lowercase()
    }

    private fun getConstraintViolationErrors(ex: ConstraintViolationException): List<Map<String, String>> {
        return ex.constraintViolations
            .map { error ->
                mapOf(
                    "field" to toSnakeCase(error.propertyPath.toString()),
                    "message" to error.message
                )
            }
            .sortedBy { it["field"] }
    }

    private fun getMethodArgumentTypeMismatchErrors(ex: MethodArgumentTypeMismatchException): List<Map<String, String>> {
        val parameter = ex.name

        return listOf(
            mapOf(
                "field" to toSnakeCase(parameter),
                "message" to "Invalid value '${ex.value}' for parameter '${parameter}'. Expected type: '${ex.requiredType?.simpleName ?: "Unknown"}'"
            )
        )
    }

    private fun getJsonParseErrors(ex: HttpMessageNotReadableException): List<Map<String, String>> {
        val cause = ex.cause

        return if (cause is InvalidFormatException) {
            val fieldName = cause.path.joinToString(".") { it.fieldName ?: "" }
            val message = "Invalid value '${cause.value}' for field '$fieldName'"

            if (cause.targetType.isEnum) {
                message + ". Expected one of: ${cause.targetType.enumConstants?.joinToString()}"
            }

            listOf(
                mapOf(
                    "field" to fieldName,
                    "message" to message
                )
            )
        } else {
            listOf(
                mapOf(
                    "field" to "unknown",
                    "message" to "Invalid JSON format or unexpected value"
                )
            )
        }
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(e: Exception): ResponseEntity<ErrorDetailsResponse> {
        log.error(e.message, e)

        val errorDetails = ErrorDetailsResponse(
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            error = "InternalServerError",
            message = "Something went wrong"
        )

        return ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}