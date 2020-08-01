package com.gabo.inventory.repositories;

import com.gabo.inventory.models.MovementHeader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MovementHeaderRepository extends MongoRepository <MovementHeader, String> {

    @Query("{'_id': {'$in':?0}}")
    Optional<List<MovementHeader>> findByIdList(Set<String> movementHeaderIdList);

    Page<MovementHeader> findById(String name, Pageable pageable);

}
