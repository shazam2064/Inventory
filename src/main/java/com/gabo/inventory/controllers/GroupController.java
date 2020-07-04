package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.GroupNotFoundException;
import com.gabo.inventory.models.Group;
import com.gabo.inventory.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.gabo.inventory.constants.InventoryConstants.GROUP_PATH;
import static com.gabo.inventory.constants.InventoryConstants.INVENTORY_V1_PATH;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(INVENTORY_V1_PATH)
public class GroupController {


    private GroupRepository groupRepository;

    @Autowired
    public GroupController(GroupRepository groupRepository) {

        this.groupRepository = groupRepository;
    }

    @GetMapping(GROUP_PATH)
    public ResponseEntity<List<Group>> getGroupList(@RequestParam(required = false)
                                                          Set<String> groupIdList) {

        if (groupIdList == null || groupIdList.isEmpty()) {

            return new ResponseEntity<>(groupRepository.findAll(), HttpStatus.OK);

        } else {

            Optional<List<Group>> optionalList = groupRepository.findByIdList(groupIdList);
            if (!optionalList.isPresent()) {

                throw new GroupNotFoundException("");
            }
            return new ResponseEntity<>(optionalList.get(), HttpStatus.OK);
        }
    }

    @GetMapping(GROUP_PATH + "/{id}")
    public ResponseEntity<Group> getGroupById(@PathVariable("id") String id) {

        Optional<Group> optionalResponse = groupRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new GroupNotFoundException(id);
        }
        return new ResponseEntity<>(optionalResponse.get(), HttpStatus.OK);
    }

    @PostMapping(GROUP_PATH)
    public @ResponseBody ResponseEntity<Group> addGroup(@Validated @RequestBody Group group) {

        groupRepository.save(group);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }


    @DeleteMapping(GROUP_PATH + "/{id}")
    public void deleteGroup(@PathVariable("id") String id) {

        Optional<Group> optionalResponse = groupRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new GroupNotFoundException(id);
        }
        groupRepository.deleteById(id);
    }

    @PutMapping(GROUP_PATH + "/{id}")
    public ResponseEntity<Group> updateGroupById(@Validated @RequestBody Group group, @PathVariable("id") String id) {

        Optional<Group> optionalResponse = groupRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new GroupNotFoundException(id);
        }
        group.id = id;
        groupRepository.save(group);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }
}
