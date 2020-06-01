package com.gabo.inventory.models;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Data
@Document("units")
public class Unit {

    @Id
    public String id;
    @NotBlank(message = "Please provide the group name.")
    public String name;
}
