package com.gabo.inventory.repositories;

import com.gabo.inventory.models.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GroupRepository extends MongoRepository <Group, String> {

    @Query("{'_id': {'$in':?0}}")
    Optional<List<Group>> findByIdList(Set<String> groupIdList);

}
