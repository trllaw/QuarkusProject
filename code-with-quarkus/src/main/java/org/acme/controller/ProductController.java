package org.acme.controller;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.entities.Product;
import org.acme.entities.ProductDTO;
import org.acme.service.ProductService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.time.LocalDate;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;

@Path("/product")
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {

    @Inject
    ProductService productService;

    @GET
    @Operation(description = "Get all Products in the database")
    public Response retrieveProducts() {
        return productService.findAllProducts();
    }

    @GET
    @Path("/search")
    @Operation(description = "Get all Products in the database and divide them in pages. The deault value for page size is 10. The deault value for page index is 1")
    public Response retrievePageOfProducts(@QueryParam("page") @DefaultValue("1") int pageIndex,
            @QueryParam("size") @DefaultValue("10") int pageSize) {
        System.out.println(pageIndex);
        System.out.println(pageSize);
        return productService.getPageOfProducts(pageIndex, pageSize);
    }

    @GET
    @Path("/{id}")
    @Operation()
    @APIResponse(responseCode = "404", description = "Product non-existent or removed.")
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
    public Response addProduct(ProductDTO product) {
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
    public Response updateProduct(@PathParam("id") Long id, ProductDTO newProduct) {
        return productService.updateProduct(id, newProduct);
    }
}