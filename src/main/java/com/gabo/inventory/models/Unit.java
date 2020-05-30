package com.gabo.inventory.models;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("units")
public class Unit {

    @Id
    public String id;
    public String name;
}
