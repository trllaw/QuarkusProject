package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import java.time.LocalDate;
import org.acme.entities.ProductDTO;
import org.apache.http.HttpHeaders;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class ProductControllerTest {

        @Test
        @Order(1)
        public void testProductEmptyGetEndPoint() {
                given().when().get("/product")
                                .then()
                                .statusCode(200)
                                .body(is("[]"));
                given().when().get("/product/1")
                                .then()
                                .statusCode(404);
                given().when().get("/product/valid")
                                .then()
                                .statusCode(200)
                                .body(is("[]"));

        }

        @Test
        @Order(2)
        public void testProductAddEndPoint() {
                // -- product 1 insert
                ProductDTO product = new ProductDTO();
                product.setName("Product 1");
                product.setQuantity(5);
                product.setExpiry_date(LocalDate.of(2025, 05, 15));
                product.setDescription("Product Description");
                given().when().body(product).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                .post("/product").then().statusCode(201);
                // -- product 1 get
                given().when().get("/product/1").then().statusCode(200)
                                .body(is(
                                                "{\"id\":1,\"name\":\"Product 1\",\"quantity\":5,\"description\":\"Product Description\",\"expiry_date\":\"2025-05-15\"}"));
                // --null product insert
                product = new ProductDTO();
                given().when().body(product).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                .post("/product").then().statusCode(400);
                // - Invalid Produc Insert
                product.setName("P");
                product.setQuantity(-1);
                product.setDescription("D");
                given().when().body(product).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                .post("/product").then().statusCode(400);

                product.setQuantity(65);
                given().when().body(product).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                .post("/product").then().statusCode(400);

                product.setName("Product 2");
                given().when().body(product).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                .post("/product").then().statusCode(400);

                product.setDescription("Another Product Description");
                given().when().body(product).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                .post("/product").then().statusCode(400);

                // -- Product 2 insert
                product.setExpiry_date(LocalDate.of(2020, 12, 06));
                given().when().body(product).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                .post("/product").then().statusCode(201);
                // -- Product 2 get
                given().when().get("/product/2").then().statusCode(200)
                                .body(is(
                                                "{\"id\":2,\"name\":\"Product 2\",\"quantity\":65,\"description\":\"Another Product Description\",\"expiry_date\":\"2020-12-06\"}"));
                // -- Product list get
                given().when().get("/product")
                                .then()
                                .statusCode(200).body(is(
                                                "[{\"id\":1,\"name\":\"Product 1\",\"quantity\":5,\"description\":\"Product Description\",\"expiry_date\":\"2025-05-15\"},{\"id\":2,\"name\":\"Product 2\",\"quantity\":65,\"description\":\"Another Product Description\",\"expiry_date\":\"2020-12-06\"}]"));
                // -- Valid Product list get
                given().when().get("/product/valid")
                                .then()
                                .statusCode(200)
                                .body(is(
                                                "[{\"id\":1,\"name\":\"Product 1\",\"quantity\":5,\"description\":\"Product Description\",\"expiry_date\":\"2025-05-15\"}]"));
        }

        @Test
        @Order(3)
        public void testUpdateProductEndPoint() {
                // -- Product 3 insert
                ProductDTO product = new ProductDTO();
                product.setName("Product 3");
                product.setQuantity(5);
                product.setExpiry_date(LocalDate.of(2021, 05, 15));
                product.setDescription("Product Description");
                given().when().body(product).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                .post("/product").then().statusCode(201);
                product = new ProductDTO();
                // -- Update invalid product
                given().when().body(product).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                .put("/product/5")
                                .then().statusCode(404);
                // -- Update Product 3 with invalid values
                given().when().body(product).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                .put("/product/3")
                                .then().statusCode(400);
                product.setName("P");
                given().when().body(product).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                .put("/product/3")
                                .then().statusCode(400);
                product.setName("Product 3 - 2");
                product.setQuantity(-1);
                given().when().body(product).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                .put("/product/3")
                                .then().statusCode(400);
                product.setQuantity(1);
                product.setDescription("new description");
                given().when().body(product).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                .put("/product/3")
                                .then().statusCode(400);
                product.setExpiry_date(LocalDate.of(2026, 05, 15));
                // -- Update Product 3
                given().when().body(product).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                .put("/product/3")
                                .then().statusCode(202);
                // -- get Product 3
                given().when().get("/product/3").then().statusCode(200)
                                .body(is(
                                                "{\"id\":3,\"name\":\"Product 3 - 2\",\"quantity\":1,\"description\":\"new description\",\"expiry_date\":\"2026-05-15\"}"));
                // -- get Valid Products
                given().when().get("/product/valid")
                                .then()
                                .statusCode(200)
                                .body(is(
                                                "[{\"id\":1,\"name\":\"Product 1\",\"quantity\":5,\"description\":\"Product Description\",\"expiry_date\":\"2025-05-15\"},{\"id\":3,\"name\":\"Product 3 - 2\",\"quantity\":1,\"description\":\"new description\",\"expiry_date\":\"2026-05-15\"}]"));
        }

        @Test
        @Order(4)
        public void testGetProductPagesEndPoint() {
                given().when().get("/product/search?page=1&size=2")
                                .then()
                                .statusCode(200)
                                .body(is("[{\"id\":1,\"name\":\"Product 1\",\"quantity\":5,\"description\":\"Product Description\",\"expiry_date\":\"2025-05-15\"},{\"id\":2,\"name\":\"Product 2\",\"quantity\":65,\"description\":\"Another Product Description\",\"expiry_date\":\"2020-12-06\"}]"));

                given().when().get("/product/search?page=2&size=2")
                                .then()
                                .statusCode(200)
                                .body(is("[{\"id\":3,\"name\":\"Product 3 - 2\",\"quantity\":1,\"description\":\"new description\",\"expiry_date\":\"2026-05-15\"}]"));

        }

        @Test
        @Order(5)
        public void testDeleteProductEndPoint() {
                // -- Delete Invalid Product
                given().when().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).delete("/product/5")
                                .then().statusCode(404);
                // Delete Product 1
                given().when().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).delete("/product/1")
                                .then().statusCode(200);
                // Get Deleted Product
                given().when().get("/product/1")
                                .then()
                                .statusCode(404);

                // Get Products
                given().when().get("/product")
                                .then()
                                .statusCode(200)
                                .body(is(
                                                "[{\"id\":2,\"name\":\"Product 2\",\"quantity\":65,\"description\":\"Another Product Description\",\"expiry_date\":\"2020-12-06\"},{\"id\":3,\"name\":\"Product 3 - 2\",\"quantity\":1,\"description\":\"new description\",\"expiry_date\":\"2026-05-15\"}]"));

                // -- Get Valid Products
                given().when().get("/product/valid")
                                .then()
                                .statusCode(200)
                                .body(is(
                                                "[{\"id\":3,\"name\":\"Product 3 - 2\",\"quantity\":1,\"description\":\"new description\",\"expiry_date\":\"2026-05-15\"}]"));
                // -- Delete Products
                given().when().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).delete("/product/2")
                                .then().statusCode(200);
                given().when().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).delete("/product/3")
                                .then().statusCode(200);

                // Get Products
                given().when().get("/product")
                                .then()
                                .statusCode(200)
                                .body(is("[]"));
                // -- Get Valid Products
                given().when().get("/product/valid")
                                .then()
                                .statusCode(200)
                                .body(is("[]"));
        }
}
