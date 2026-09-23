package com.example.products

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
class ProductController(private val products: ProductRepository) {

    @GetMapping
    fun list(): List<Product> = products.findAll()

    @PostMapping
    fun create(@RequestBody request: CreateProductRequest): ResponseEntity<Product> {
        if (request.name.isBlank()) {
            return ResponseEntity.badRequest().build()
        }
        val saved = products.save(Product(name = request.name.trim()))
        return ResponseEntity.status(201).body(saved)
    }
}

data class CreateProductRequest(val name: String)
