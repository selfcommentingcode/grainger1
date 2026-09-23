package com.example.products

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Persistence-layer tests for [ProductRepository] and the [Product] entity mapping.
 * Runs against in-memory H2 (see src/test/resources/application.properties);
 * @AutoConfigureTestDatabase(replace = NONE) keeps that configured datasource
 * so schema.sql creates the table. @DataJpaTest rolls back after each test.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    lateinit var repository: ProductRepository

    @Test
    fun `save assigns a generated id and findAll returns the persisted product`() {
        val saved = repository.save(Product(name = "Widget"))

        assertNotNull(saved.id, "id should be generated on save")

        val all = repository.findAll()
        assertTrue(
            all.any { it.id == saved.id && it.name == "Widget" },
            "findAll should contain the saved product"
        )
    }

    @Test
    fun `findAll returns an empty list when there are no products`() {
        repository.deleteAll()
        assertEquals(0, repository.findAll().size)
    }
}
