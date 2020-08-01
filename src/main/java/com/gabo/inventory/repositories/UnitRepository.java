package com.gabo.inventory.repositories;

import com.gabo.inventory.models.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UnitRepository extends MongoRepository <Unit, String> {

    @Query("{'_id': {'$in':?0}}")
    Optional<List<Unit>> findByIdList(Set<String> unitIdList);

    Page<Unit> findByNameContaining(String name, Pageable pageable);

}
