package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.MovementTypeNotFoundException;
import com.gabo.inventory.exceptions.MovementTypePageParameterException;
import com.gabo.inventory.models.MovementType;
import com.gabo.inventory.repositories.MovementTypeRepository;
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
public class MovementTypeController {

    private MovementTypeRepository movementTypeRepository;

    @Autowired
    public MovementTypeController(MovementTypeRepository movementTypeRepository) {
        this.movementTypeRepository = movementTypeRepository;
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }

    @GetMapping(MOVEMENT_TYPE_PATH)
    public ResponseEntity<List<MovementType>> getAllMovementType(@RequestParam(defaultValue = "id,asc") String[] sort) {

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

            List<MovementType> movementTypes = movementTypeRepository.findAll(Sort.by(orders));

            if (movementTypes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(movementTypes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(MOVEMENT_TYPE_PAGE_PATH)
    public ResponseEntity<Map<String, Object>> getPageMovementTypes(
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

            List<MovementType> movementTypes = new ArrayList<MovementType>();
            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

            Page<MovementType> pageTuts;
            if (name == null)
                pageTuts = movementTypeRepository.findAll(pagingSort);
            else
                pageTuts = movementTypeRepository.findByNameContaining(name, pagingSort);

            movementTypes = pageTuts.getContent();

            if (movementTypes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            Map<String, Object> response = new HashMap<>();

            response.put("movementTypes", movementTypes);
            response.put("currentPage", pageTuts.getNumber());
            response.put("totalMovementTypes", pageTuts.getTotalElements());
            response.put("totalPages", pageTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(MOVEMENT_TYPE_PATH + "/{id}")
    public ResponseEntity<MovementType> getMovementTypeById(@PathVariable("id") String id) {

        Optional<MovementType> optionalResponse = movementTypeRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new MovementTypeNotFoundException(id);
        }
        return new ResponseEntity<>(optionalResponse.get(), HttpStatus.OK);
    }

    @PostMapping(MOVEMENT_TYPE_PATH)
    public @ResponseBody ResponseEntity<MovementType> addMovementType(@Validated @RequestBody MovementType movementType) {
        movementTypeRepository.save(movementType);
        return new ResponseEntity<>(movementType, HttpStatus.OK);
    }

    @DeleteMapping(MOVEMENT_TYPE_PATH + "/{id}")
    public void deleteMovementType(@PathVariable("id") String id) {

        Optional<MovementType> optionalResponse = movementTypeRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new MovementTypeNotFoundException(id);
        }
        movementTypeRepository.deleteById(id);
    }

    @PutMapping(MOVEMENT_TYPE_PATH + "/{id}")
    public ResponseEntity<MovementType> updateMovementTypeById(@Validated @RequestBody MovementType movementType, @PathVariable("id") String id) {

        Optional<MovementType> optionalResponse = movementTypeRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new MovementTypeNotFoundException(id);
        }
        movementType.id = id;
        movementTypeRepository.save(movementType);
        return new ResponseEntity<>(movementType, HttpStatus.OK);
    }

}