package org.acme.entities;

import java.time.LocalDate;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;

@Entity
@Cacheable
public class Product extends PanacheEntity {
    @Schema(description = "Product Name.")
    private String name;
    @Schema(defaultValue = "0", description = "Product Quantity in stock. Must be a positive value.")
    private long quantity;
    @Schema(description = "Product description")
    private String description;
    @Schema(description = "Product expiry date")
    private LocalDate expiry_date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(LocalDate expiry_date) {
        this.expiry_date = expiry_date;
    }

}
