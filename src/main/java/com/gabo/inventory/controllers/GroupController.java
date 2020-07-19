package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.GroupNotFoundException;
import com.gabo.inventory.exceptions.GroupPageParameterException;
import com.gabo.inventory.models.Group;
import com.gabo.inventory.repositories.GroupRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.gabo.inventory.constants.InventoryConstants.INVENTORY_V1_PATH;
import static com.gabo.inventory.constants.InventoryConstants.GROUP_PATH;

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
    public ResponseEntity<List<Group>> GetPagedGroupList(
            @RequestParam(value = "groupList", required = false) Set<String> requestedGroupList,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {

        List<Group> groupList;

        if (requestedGroupList == null || requestedGroupList.isEmpty()) {

            groupList = getPagedGroupList(page, size);
        } else {

            groupList = getFilteredGroupList(requestedGroupList);
        }

        return new ResponseEntity<>(groupList, HttpStatus.OK);
    }

    private List<Group> getFilteredGroupList(Set<String> requestedGroupList) {

        List<Group> groupList;
        Optional<List<Group>> optionalList = groupRepository.findByIdList(requestedGroupList);
        if (!optionalList.isPresent()) {

            throw new GroupNotFoundException("");
        }
        groupList = optionalList.get();
        return groupList;
    }

    private List<Group> getPagedGroupList(Integer page, Integer size) {

        if (page == null ^ size == null) {
            throw new GroupPageParameterException();
        }

        List<Group> groupList;
        if (page == null) {

            groupList = groupRepository.findAll();
        } else {

            Pageable pageable = PageRequest.of(page, size);
            Page<Group> requestedPage = groupRepository.findAll(pageable);
            groupList = Lists.newArrayList(requestedPage);
        }
        return groupList;
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
    public @ResponseBody
    ResponseEntity<Group> addGroup(@Validated @RequestBody Group group) {
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