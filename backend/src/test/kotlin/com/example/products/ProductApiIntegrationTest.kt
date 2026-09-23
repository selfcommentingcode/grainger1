package com.example.products

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

/**
 * Full-stack integration test: real Spring context (loads [ProductsApplication]
 * and [WebConfig]) driving [ProductController] → [ProductRepository] → H2 via
 * MockMvc. @Transactional rolls back each test's writes so they stay isolated.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductApiIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `context loads and GET returns a JSON array`() {
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `create then list returns the persisted product end-to-end`() {
        mockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"P1"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isNumber)
            .andExpect(jsonPath("$.name").value("P1"))

        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[?(@.name == 'P1')]").exists())
    }

    @Test
    fun `blank name is rejected with 400 end-to-end`() {
        mockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"   "}""")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `CORS preflight from the frontend origin is allowed`() {
        mockMvc.perform(
            options("/api/products")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
        )
            .andExpect(status().isOk)
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
    }
}
