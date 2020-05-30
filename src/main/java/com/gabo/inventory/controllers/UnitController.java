package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.UnitNotFoundException;
import com.gabo.inventory.models.Unit;
import com.gabo.inventory.repositories.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.gabo.inventory.constants.InventoryConstants.UNIT_PATH;
import static com.gabo.inventory.constants.InventoryConstants.INVENTORY_V1_PATH;

@RestController
@RequestMapping(INVENTORY_V1_PATH)
public class UnitController {


    private UnitRepository unitRepository;

    @Autowired
    public UnitController(UnitRepository unitRepository) {

        this.unitRepository = unitRepository;
    }

    @GetMapping(UNIT_PATH)
    public ResponseEntity<List<Unit>> getUnitList(@RequestParam(required = false)
                                                          Set<String> unitIdList) {

        if (unitIdList == null || unitIdList.isEmpty()) {

            return new ResponseEntity<>(unitRepository.findAll(), HttpStatus.OK);

        } else {

            Optional<List<Unit>> optionalList = unitRepository.findByIdList(unitIdList);
            if (!optionalList.isPresent()) {

                throw new UnitNotFoundException("");
            }
            return new ResponseEntity<>(optionalList.get(), HttpStatus.OK);
        }
    }

    @GetMapping(UNIT_PATH + "/{id}")
    public ResponseEntity<Unit> getUnitById(@PathVariable("id") String id) {

        Optional<Unit> optionalResponse = unitRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new UnitNotFoundException(id);
        }
        return new ResponseEntity<>(optionalResponse.get(), HttpStatus.OK);
    }
}
