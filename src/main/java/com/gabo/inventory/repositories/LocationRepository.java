package com.gabo.inventory.repositories;

import com.gabo.inventory.models.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LocationRepository extends MongoRepository <Location, String> {

    @Query("{'_id': {'$in':?0}}")
    Optional<List<Location>> findByIdList(Set<String> locationIdList);

    Page<Location> findByAisleContaining(String name, Pageable pageable);

}