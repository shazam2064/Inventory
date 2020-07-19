package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.UnitNotFoundException;
import com.gabo.inventory.exceptions.UnitPageParameterException;
import com.gabo.inventory.models.Unit;
import com.gabo.inventory.repositories.UnitRepository;
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
import static com.gabo.inventory.constants.InventoryConstants.UNIT_PATH;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(INVENTORY_V1_PATH)
public class UnitController {

    private UnitRepository unitRepository;

    @Autowired
    public UnitController(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @GetMapping(UNIT_PATH)
    public ResponseEntity<List<Unit>> getPagedUnitList(
            @RequestParam(value = "unitList", required = false) Set<String> requestedUnitList,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {

        List<Unit> unitList;

        if (requestedUnitList == null || requestedUnitList.isEmpty()) {

            unitList = getPagedUnitList(page, size);
        } else {

            unitList = getFilteredUnitList(requestedUnitList);
        }

        return new ResponseEntity<>(unitList, HttpStatus.OK);
    }

    private List<Unit> getFilteredUnitList(Set<String> requestedUnitList) {

        List<Unit> unitList;
        Optional<List<Unit>> optionalList = unitRepository.findByIdList(requestedUnitList);
        if (!optionalList.isPresent()) {

            throw new UnitNotFoundException("");
        }
        unitList = optionalList.get();
        return unitList;
    }

    private List<Unit> getPagedUnitList(Integer page, Integer size) {

        if (page == null ^ size == null) {
            throw new UnitPageParameterException();
        }

        List<Unit> unitList;
        if (page == null) {

            unitList = unitRepository.findAll();
        } else {

            Pageable pageable = PageRequest.of(page, size);
            Page<Unit> requestedPage = unitRepository.findAll(pageable);
            unitList = Lists.newArrayList(requestedPage);
        }
        return unitList;
    }

    @GetMapping(UNIT_PATH + "/{id}")
    public ResponseEntity<Unit> getUnitById(@PathVariable("id") String id) {

        Optional<Unit> optionalResponse = unitRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new UnitNotFoundException(id);
        }
        return new ResponseEntity<>(optionalResponse.get(), HttpStatus.OK);
    }

    @PostMapping(UNIT_PATH)
    public @ResponseBody
    ResponseEntity<Unit> addUnit(@Validated @RequestBody Unit unit) {
        unitRepository.save(unit);
        return new ResponseEntity<>(unit, HttpStatus.OK);
    }

    @DeleteMapping(UNIT_PATH + "/{id}")
    public void deleteUnit(@PathVariable("id") String id) {

        Optional<Unit> optionalResponse = unitRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new UnitNotFoundException(id);
        }
        unitRepository.deleteById(id);
    }

    @PutMapping(UNIT_PATH + "/{id}")
    public ResponseEntity<Unit> updateUnitById(@Validated @RequestBody Unit unit, @PathVariable("id") String id) {

        Optional<Unit> optionalResponse = unitRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new UnitNotFoundException(id);
        }
        unit.id = id;
        unitRepository.save(unit);
        return new ResponseEntity<>(unit, HttpStatus.OK);
    }

}