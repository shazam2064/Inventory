package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.MovementHeaderNotFoundException;
import com.gabo.inventory.exceptions.MovementHeaderPageParameterException;
import com.gabo.inventory.models.MovementHeader;
import com.gabo.inventory.repositories.MovementHeaderRepository;
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

    @GetMapping(MOVEMENT_HEADER_PATH)
    public ResponseEntity<List<MovementHeader>> getPagedMovementHeaderList(
            @RequestParam(value = "movementHeaderList", required = false) Set<String> requestedMovementHeaderList,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {

        List<MovementHeader> movementHeaderList;

        if (requestedMovementHeaderList == null || requestedMovementHeaderList.isEmpty()) {

            movementHeaderList = getPagedMovementHeaderList(page, size);
        } else {

            movementHeaderList = getFilteredMovementHeaderList(requestedMovementHeaderList);
        }

        return new ResponseEntity<>(movementHeaderList, HttpStatus.OK);
    }

    private List<MovementHeader> getFilteredMovementHeaderList(Set<String> requestedMovementHeaderList) {

        List<MovementHeader> movementHeaderList;
        Optional<List<MovementHeader>> optionalList = movementHeaderRepository.findByIdList(requestedMovementHeaderList);
        if (!optionalList.isPresent()) {

            throw new MovementHeaderNotFoundException("");
        }
        movementHeaderList = optionalList.get();
        return movementHeaderList;
    }

    private List<MovementHeader> getPagedMovementHeaderList(Integer page, Integer size) {

        if (page == null ^ size == null) {
            throw new MovementHeaderPageParameterException();
        }

        List<MovementHeader> movementHeaderList;
        if (page == null) {

            movementHeaderList = movementHeaderRepository.findAll();
        } else {

            Pageable pageable = PageRequest.of(page, size);
            Page<MovementHeader> requestedPage = movementHeaderRepository.findAll(pageable);
            movementHeaderList = Lists.newArrayList(requestedPage);
        }
        return movementHeaderList;
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
