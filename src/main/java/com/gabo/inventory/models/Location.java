package com.gabo.inventory.models;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Data
@Document("locations")
public class Location {

    @Id
    public String id;
    @NotBlank(message = "Please provide the location name.")
    public String aisle;
    public String rack;
    public String shelf;
}
