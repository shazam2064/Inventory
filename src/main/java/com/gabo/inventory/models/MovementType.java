package com.gabo.inventory.models;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("movement_types")
public class MovementType {

    @Id
    public String id;
    public String name;
}
