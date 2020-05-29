package com.gabo.inventory.models;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("warehouses")
public class Warehouse {

    public String id;
    public String name;
}
