package io.tolgee.fixtures

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.tolgee.assertions.Assertions.assertThat
import io.tolgee.assertions.MvcResultAssert
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

val ResultActions.andIsOk: ResultActions
    get() = this.andExpect(status().isOk)

val ResultActions.andIsCreated: ResultActions
    get() = this.andExpect(status().isCreated)

val ResultActions.andIsBadRequest: ResultActions
    get() = this.andExpect(status().isBadRequest)


val ResultActions.andIsForbidden: ResultActions
    get() = this.andExpect(status().isForbidden)


val ResultActions.andAssertResponse: MvcResultAssert
    get() = assertThat(this.andReturn())

val ResultActions.andAssertThatJson
    get() = assertThatJson(this.andReturn().response.contentAsString)

val ResultActions.andGetContentAsString
    get() = this.andReturn().response.contentAsString

val ResultActions.andAssertError
    get() = assertThat(this.andReturn()).error()


val ResultActions.andPrettyPrint: ResultActions
    get() = jacksonObjectMapper().let { mapper ->
        val parsed = mapper.readValue<Any>(this.andGetContentAsString)
        println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsed))
        return this
    }