package org.acme.controller;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import java.util.ArrayList;
import java.util.List;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.entities.Product;
import org.acme.service.ProductService;
import java.time.LocalDate;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;

@Path("/product")
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {

    @Inject
    ProductService productService;

    @GET
    public Response retrieveProducts() {
        return productService.findAllProducts();
    }

    @GET
    @Path("/{id}")
    public Response retrieveProductById(@PathParam("id") Long id) {
        return productService.findProductById(id);
    }

    @GET
    @Path("/valid")
    public List<Product> retrieveValidProducts() {
        LocalDate date = LocalDate.now();
        List<Product> products = new ArrayList<>();
        try {
            products = productService.findValidProducts(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addProduct(Product product) {
        return productService.addProduct(product);
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    public Response deleteByProduct(@PathParam("id") Long id) {
        return productService.removeProductById(id);
    }

    @PUT
    @Transactional
    @Path("/{id}")
    public Response updateProduct(@PathParam("id") Long id, Product newProduct) {
        return productService.updateProduct(id, newProduct);
    }
}