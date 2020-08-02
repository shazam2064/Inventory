package com.gabo.inventory.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@Document("items")
public class Item {

    protected Item() { }

    @Id
    public String id;

    @NotBlank(message = "Please provide the item name.")
    public String name;

    public String description;
    public String brand;
    public String unit;
    public String group;
    public String location;
    public String warehouse;
    public int min;
    public int max;
    public int reorderPoint;
    public Date entryDate;
    public Date departureDate;
    public int ultimateValue;
}
