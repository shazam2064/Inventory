package com.gabo.inventory.models;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("groups")
public class Group {

    @Id
    public String id;
    public String name;
}
