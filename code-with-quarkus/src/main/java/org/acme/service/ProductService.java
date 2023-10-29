package org.acme.service;

import org.acme.entities.Product;
import org.acme.entities.ProductDTO;
import org.acme.repository.ProductRepository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class ProductService {
    @Inject
    ProductRepository productRepository;

    public Response findAllProducts() {
        return Response.ok(productRepository.findAll(Sort.by("id")).list()).build();
    }

    public List<Product> findValidProducts(LocalDate date) {

        return productRepository.find("expiry_date > :now order by id", Parameters.with("now", date)).list();
    }

    public Response addProduct(ProductDTO product) {
        if (Objects.isNull(product.getQuantity()) || product.getQuantity() < 0)
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("The Product must have a quantity that is a positive value").build();
        if (Objects.isNull(product.getName()) || product.getName().length() < 5)
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("The Product must have a name with 5 or more characters").build();
        if (Objects.isNull(product.getDescription()) || product.getDescription().length() < 5)
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("The Product must have a description with 5 or more characters").build();
        if (Objects.isNull(product.getExpiry_date()))
            return Response.status(Response.Status.BAD_REQUEST).entity("The Product must have a expiry date").build();

        Product newProduct = new Product();
        newProduct.setName(product.getName());
        newProduct.setDescription(product.getDescription());
        newProduct.setExpiry_date(product.getExpiry_date());
        newProduct.setQuantity(newProduct.getQuantity());
        productRepository.persist(newProduct);

        if (productRepository.isPersistent(newProduct))
            return Response.created(URI.create("/product/" + newProduct.id)).build();
        else
            return Response.status(Response.Status.BAD_REQUEST).build();
    }

    public Response removeProductById(Long id) {
        Optional<Product> optionalProduct = productRepository.findByIdOptional(id);
        if (!optionalProduct.isPresent())
            return Response.status(Response.Status.NOT_FOUND).build();
        if (productRepository.deleteById(id))
            return Response.ok(URI.create("/product/")).build();
        else
            return Response.notModified().build();
    }

    public Response findProductById(Long id) {
        Optional<Product> optionalProduct = productRepository.findByIdOptional(id);
        if (optionalProduct.isPresent())
            return Response.ok(optionalProduct.get()).build();

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    public Response updateProduct(Long id, ProductDTO newProduct) {
        Optional<Product> optionalProduct = productRepository.findByIdOptional(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if (Objects.nonNull(newProduct.getName()))
                product.setName(newProduct.getName());
            else
                return Response.status(Response.Status.BAD_REQUEST).build();

            if (Objects.nonNull(newProduct.getDescription()))
                product.setDescription(newProduct.getDescription());
            else
                return Response.status(Response.Status.BAD_REQUEST).build();

            if (Objects.nonNull(newProduct.getExpiry_date()))
                product.setExpiry_date(newProduct.getExpiry_date());
            else
                return Response.status(Response.Status.BAD_REQUEST).build();

            if (Objects.nonNull(newProduct.getQuantity()) && newProduct.getQuantity() >= 0)
                product.setQuantity(newProduct.getQuantity());
            else
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("The Product must hava a quantity that is a positive value").build();

            productRepository.persist(product);
            if (productRepository.isPersistent(product))
                return Response.accepted(URI.create("/product/" + product.id)).build();
            else
                return Response.notModified().build();

        } else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

    public Response getPageOfProducts(int pageIndex, int pageSize) {
        PanacheQuery<Product> products = productRepository.findAll();
        products.page(Page.ofSize(pageSize));
        System.out.println(products.pageCount());
        System.out.println(pageIndex);
        System.out.println(pageSize);
        return Response.ok(products.page(Page.of(pageIndex - 1, pageSize)).list()).build();
    }
}
