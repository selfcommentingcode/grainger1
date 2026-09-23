package com.example.products

import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals

/**
 * Controller-layer unit tests: the HTTP contract of [ProductController] with the
 * repository mocked out (no database, no Spring Data). Fast and isolated.
 */
@WebMvcTest(ProductController::class)
class ProductControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var repository: ProductRepository

    @Test
    fun `GET returns the products as a JSON array`() {
        whenever(repository.findAll()).thenReturn(
            listOf(Product(name = "P1", id = 1L), Product(name = "P2", id = 2L))
        )

        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("P1"))
    }

    @Test
    fun `POST with a valid name returns 201 and the saved product`() {
        whenever(repository.save(any<Product>())).thenAnswer { invocation ->
            (invocation.arguments[0] as Product).apply { id = 42L }
        }

        mockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Widget"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.name").value("Widget"))
    }

    @Test
    fun `POST trims surrounding whitespace before saving`() {
        whenever(repository.save(any<Product>())).thenAnswer { it.arguments[0] }

        mockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"  Widget  "}""")
        )
            .andExpect(status().isCreated)

        val captor = argumentCaptor<Product>()
        verify(repository).save(captor.capture())
        assertEquals("Widget", captor.firstValue.name, "name should be trimmed before persisting")
    }

    @Test
    fun `POST with a blank name returns 400 and never touches the repository`() {
        mockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"   "}""")
        )
            .andExpect(status().isBadRequest)

        verify(repository, never()).save(any<Product>())
    }
}
