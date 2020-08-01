package com.gabo.inventory.repositories;

import com.gabo.inventory.models.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface WarehouseRepository extends MongoRepository <Warehouse, String> {

    @Query("{'_id': {'$in':?0}}")
    Optional<List<Warehouse>> findByIdList(Set<String> warehouseIdList);

    Page<Warehouse> findByNameContaining(String name, Pageable pageable);

}
