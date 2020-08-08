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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.gabo.inventory.constants.InventoryConstants.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(INVENTORY_V1_PATH)
public class GroupController {

    private GroupRepository groupRepository;

    @Autowired
    public GroupController(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }

    @GetMapping(GROUP_PATH)
    public ResponseEntity<List<Group>> getAllGroup(@RequestParam(defaultValue = "id,asc") String[] sort) {

        try {
            List<Sort.Order> orders = new ArrayList<Sort.Order>();

            if (sort[0].contains(",")) {
                // will sort more than 2 fields
                // sortOrder="field, direction"
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                // sort=[field, direction]
                orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
            }

            List<Group> groups = groupRepository.findAll(Sort.by(orders));

            if (groups.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(groups, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(GROUP_PAGE_PATH)
    public ResponseEntity<Map<String, Object>> getPageGroups(
            @RequestParam(required = false) String name,
            @RequestParam(required = false/*, defaultValue = "0"*/) Integer page,
            @RequestParam(required = false/*, defaultValue = "3"*/) Integer size,
            @RequestParam(required = false, defaultValue = "id,asc") String[] sort) {


        try {
            List<Sort.Order> orders = new ArrayList<Sort.Order>();

            if (sort[0].contains(",")) {
                // will sort more than 2 fields
                // sortOrder="field, direction"
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                // sort=[field, direction]
                orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
            }

            List<Group> groups = new ArrayList<Group>();
            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

            Page<Group> pageTuts;
            if (name == null)
                pageTuts = groupRepository.findAll(pagingSort);
            else
                pageTuts = groupRepository.findByNameContaining(name, pagingSort);

            groups = pageTuts.getContent();

            if (groups.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            Map<String, Object> response = new HashMap<>();

            response.put("groups", groups);
            response.put("currentPage", pageTuts.getNumber());
            response.put("totalGroups", pageTuts.getTotalElements());
            response.put("totalPages", pageTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
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