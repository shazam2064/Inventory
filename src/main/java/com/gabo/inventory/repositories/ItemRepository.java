package com.gabo.inventory.repositories;

import com.gabo.inventory.models.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ItemRepository extends MongoRepository <Item, String> {

    @Query("{'_id': {'$in':?0}}")
    Optional<List<Item>> findByIdList(Set<String> itemIdList);

}
