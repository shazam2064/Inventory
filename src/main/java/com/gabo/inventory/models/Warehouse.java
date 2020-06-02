package com.gabo.inventory.models;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Data
@Document("warehouses")
public class Warehouse {

    public String id;
    @NotBlank(message = "Please provide the warehouse name.")
    public String name;
}
