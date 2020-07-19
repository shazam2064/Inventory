package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.MovementTypeNotFoundException;
import com.gabo.inventory.exceptions.MovementTypePageParameterException;
import com.gabo.inventory.exceptions.UnitNotFoundException;
import com.gabo.inventory.exceptions.UnitPageParameterException;
import com.gabo.inventory.models.MovementType;
import com.gabo.inventory.models.Unit;
import com.gabo.inventory.repositories.MovementTypeRepository;
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
import static com.gabo.inventory.constants.InventoryConstants.MOVEMENT_TYPE_PATH;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(INVENTORY_V1_PATH)
public class MovementTypeController {


    private MovementTypeRepository movementTypeRepository;

    @Autowired
    public MovementTypeController(MovementTypeRepository movementTypeRepository) {

        this.movementTypeRepository = movementTypeRepository;
    }

    @GetMapping(MOVEMENT_TYPE_PATH)
    public ResponseEntity<List<MovementType>> getPagedMovementTypeList(
            @RequestParam(value = "movementTypeList", required = false) Set<String> requestedMovementTypeList,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {

        List<MovementType> movementTypeList;

        if (requestedMovementTypeList == null || requestedMovementTypeList.isEmpty()) {

            movementTypeList = getPagedMovementTypeList(page, size);
        } else {

            movementTypeList = getFilteredMovementTypeList(requestedMovementTypeList);
        }

        return new ResponseEntity<>(movementTypeList, HttpStatus.OK);
    }

    private List<MovementType> getFilteredMovementTypeList(Set<String> requestedMovementTypeList) {

        List<MovementType> movementTypeList;
        Optional<List<MovementType>> optionalList = movementTypeRepository.findByIdList(requestedMovementTypeList);
        if (!optionalList.isPresent()) {

            throw new MovementTypeNotFoundException("");
        }
        movementTypeList = optionalList.get();
        return movementTypeList;
    }

    private List<MovementType> getPagedMovementTypeList(Integer page, Integer size) {

        if (page == null ^ size == null) {
            throw new MovementTypePageParameterException();
        }

        List<MovementType> movementTypeList;
        if (page == null) {

            movementTypeList = movementTypeRepository.findAll();
        } else {

            Pageable pageable = PageRequest.of(page, size);
            Page<MovementType> requestedPage = movementTypeRepository.findAll(pageable);
            movementTypeList = Lists.newArrayList(requestedPage);
        }
        return movementTypeList;
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
