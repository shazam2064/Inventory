package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.MovementHeaderNotFoundException;
import com.gabo.inventory.exceptions.MovementHeaderPageParameterException;
import com.gabo.inventory.models.MovementHeader;
import com.gabo.inventory.models.MovementHeader;
import com.gabo.inventory.repositories.MovementHeaderRepository;
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

import static com.gabo.inventory.constants.InventoryConstants.INVENTORY_V1_PATH;
import static com.gabo.inventory.constants.InventoryConstants.MOVEMENT_HEADER_PATH;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(INVENTORY_V1_PATH)
public class MovementHeaderController {


    private MovementHeaderRepository movementHeaderRepository;

    @Autowired
    public MovementHeaderController(MovementHeaderRepository movementHeaderRepository) {

        this.movementHeaderRepository = movementHeaderRepository;
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }
    
    @GetMapping(MOVEMENT_HEADER_PATH)
    public ResponseEntity<Map<String, Object>> getAllMovementHeaders(
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

            Map<String, Object> responseAll = new HashMap<>();

            List<MovementHeader> movementHeaders;
            if (page == null) {
                movementHeaders = movementHeaderRepository.findAll();
                responseAll.put("movementHeaders", movementHeaders);
                return new ResponseEntity<>(responseAll, HttpStatus.OK);
            }

            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

            Page<MovementHeader> pageTuts;
            if (name == null)
                pageTuts = movementHeaderRepository.findAll(pagingSort);
            else
                pageTuts = movementHeaderRepository.findById(name, pagingSort);

            movementHeaders = pageTuts.getContent();

            if (movementHeaders.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            Map<String, Object> response = new HashMap<>();

            response.put("movementHeaders", movementHeaders);
            response.put("currentPage", pageTuts.getNumber());
            response.put("totalMovementHeaders", pageTuts.getTotalElements());
            response.put("totalPages", pageTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping(MOVEMENT_HEADER_PATH + "/{id}")
    public ResponseEntity<MovementHeader> getMovementHeaderById(@PathVariable("id") String id) {

        Optional<MovementHeader> optionalResponse = movementHeaderRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new MovementHeaderNotFoundException(id);
        }
        return new ResponseEntity<>(optionalResponse.get(), HttpStatus.OK);
    }

    @PostMapping(MOVEMENT_HEADER_PATH)
    public @ResponseBody ResponseEntity<MovementHeader> addMovementHeader(@Validated @RequestBody MovementHeader movementHeader) {

        movementHeaderRepository.save(movementHeader);
        return new ResponseEntity<>(movementHeader, HttpStatus.OK);
    }


    @DeleteMapping(MOVEMENT_HEADER_PATH + "/{id}")
    public void deleteMovementHeader(@PathVariable("id") String id) {

        Optional<MovementHeader> optionalResponse = movementHeaderRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new MovementHeaderNotFoundException(id);
        }
        movementHeaderRepository.deleteById(id);
    }

    @PutMapping(MOVEMENT_HEADER_PATH + "/{id}")
    public ResponseEntity<MovementHeader> updateMovementHeaderById(@Validated @RequestBody MovementHeader movementHeader, @PathVariable("id") String id) {

        Optional<MovementHeader> optionalResponse = movementHeaderRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new MovementHeaderNotFoundException(id);
        }
        movementHeader.id = id;
        movementHeaderRepository.save(movementHeader);
        return new ResponseEntity<>(movementHeader, HttpStatus.OK);
    }

}
