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
    @Operation(description = "Get all Products in the database if it has any")
    @APIResponse(responseCode = "200", description = "Return a list with all Products in the database if it has any")
    public Response retrieveProducts() {
        return productService.findAllProducts();
    }

    @GET
    @Path("/search")
    @Operation(description = "Get all Products in the database and divide them in pages. The deault value for page size is 10. The deault value for page index is 1")
    @APIResponse(responseCode = "200", description = "Return the requested page with the a list of products from that page")
    public Response retrievePageOfProducts(@QueryParam("page") @DefaultValue("1") int pageIndex,
            @QueryParam("size") @DefaultValue("10") int pageSize) {
        System.out.println(pageIndex);
        System.out.println(pageSize);
        return productService.getPageOfProducts(pageIndex, pageSize);
    }

    @GET
    @Path("/{id}")
    @Operation(description = "Try to get the product with the parameter ID")
    @APIResponse(responseCode = "404", description = "Product non-existent or removed.")
    @APIResponse(responseCode = "200", description = "Get the Product that has the same ID")
    public Response retrieveProductById(@PathParam("id") Long id) {
        return productService.findProductById(id);
    }

    @GET
    @Path("/valid")
    @Operation(description = "Get all the Products that have not expired")
    @APIResponse(responseCode = "200", description = "Get a list with the Products that have not expired")
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
    @Operation(description = "Add a new Product if Valid")
    @APIResponse(responseCode = "400", description = "The product is invalid. Verify if all the values are valid.")
    @APIResponse(responseCode = "201", description = "The Product was added successfully. Return a URI to the Product.")
    public Response addProduct(ProductDTO product) {
        return productService.addProduct(product);
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @Operation(description = "Remove the Product with the parameter ID if possible.")
    @APIResponse(responseCode = "404", description = "The product Was not found to be removed.")
    @APIResponse(responseCode = "200", description = "The Product was Removed successfully.")
    public Response deleteByProduct(@PathParam("id") Long id) {
        return productService.removeProductById(id);
    }

    @PUT
    @Transactional
    @Path("/{id}")
    @Operation(description = "Update the Product with the parameter ID if possible.")
    @APIResponse(responseCode = "400", description = "The product is invalid. Verify if all the values are valid.")
    @APIResponse(responseCode = "404", description = "The product Was not found to be Updated.")
    @APIResponse(responseCode = "202", description = "The Product was updated successfully. Return a URI to the Product.")
    public Response updateProduct(@PathParam("id") Long id, ProductDTO newProduct) {
        return productService.updateProduct(id, newProduct);
    }
}