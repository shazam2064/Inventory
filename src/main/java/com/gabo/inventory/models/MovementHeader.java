package com.gabo.inventory.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("movementHeader")
public class MovementHeader {
    @Id
    public String id;
    public Date dateMovHeader;
    public Date hourMovHeader;
    public int total;
    public String docType;
    public int numDoc;
}
