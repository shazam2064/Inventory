package com.gabo.inventory.models;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("locations")
public class Location {

    @Id
    public String id;
    public String aisle;
    public String rack;
    public String shelf;
}
