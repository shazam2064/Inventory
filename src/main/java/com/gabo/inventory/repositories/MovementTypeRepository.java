package com.gabo.inventory.repositories;

import com.gabo.inventory.models.MovementType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MovementTypeRepository extends MongoRepository <MovementType, String> {

    @Query("{'_id': {'$in':?0}}")
    Optional<List<MovementType>> findByIdList(Set<String> movementTypeIdList);

}
