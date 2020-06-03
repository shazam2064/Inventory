package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.MovementTypeNotFoundException;
import com.gabo.inventory.models.MovementType;
import com.gabo.inventory.repositories.MovementTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.gabo.inventory.constants.InventoryConstants.INVENTORY_V1_PATH;
import static com.gabo.inventory.constants.InventoryConstants.MOVEMENT_TYPE_PATH;

@RestController
@RequestMapping(INVENTORY_V1_PATH)
public class MovementTypeController {


    private MovementTypeRepository movementTypeRepository;

    @Autowired
    public MovementTypeController(MovementTypeRepository movementTypeRepository) {

        this.movementTypeRepository = movementTypeRepository;
    }

    @GetMapping(MOVEMENT_TYPE_PATH)
    public ResponseEntity<List<MovementType>> getMovementTypeList(@RequestParam(required = false)
                                                          Set<String> movementTypeIdList) {

        if (movementTypeIdList == null || movementTypeIdList.isEmpty()) {

            return new ResponseEntity<>(movementTypeRepository.findAll(), HttpStatus.OK);

        } else {

            Optional<List<MovementType>> optionalList = movementTypeRepository.findByIdList(movementTypeIdList);
            if (!optionalList.isPresent()) {

                throw new MovementTypeNotFoundException("");
            }
            return new ResponseEntity<>(optionalList.get(), HttpStatus.OK);
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
        movementTypeRepository.save(movementType);
        return new ResponseEntity<>(movementType, HttpStatus.OK);
    }

}
