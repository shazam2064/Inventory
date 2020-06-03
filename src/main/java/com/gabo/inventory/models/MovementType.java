package com.gabo.inventory.models;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Data
@Document("movement_types")
public class MovementType {

    @Id
    public String id;
    @NotBlank(message = "Please provide the movement-type name.")
    public String name;
}
