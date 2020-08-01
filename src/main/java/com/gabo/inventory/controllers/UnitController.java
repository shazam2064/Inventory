package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.UnitNotFoundException;
import com.gabo.inventory.models.Unit;
import com.gabo.inventory.repositories.UnitRepository;
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

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }

    @GetMapping(UNIT_PATH)
    public ResponseEntity<Map<String, Object>> getAllUnits(
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

            List<Unit> units;
            if (page == null) {
                units = unitRepository.findAll();
                responseAll.put("units", units);
                return new ResponseEntity<>(responseAll, HttpStatus.OK);
            }

            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

            Page<Unit> pageTuts;
            if (name == null)
                pageTuts = unitRepository.findAll(pagingSort);
            else
                pageTuts = unitRepository.findByNameContaining(name, pagingSort);

            units = pageTuts.getContent();

            if (units.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            Map<String, Object> response = new HashMap<>();

            response.put("units", units);
            response.put("currentPage", pageTuts.getNumber());
            response.put("totalUnits", pageTuts.getTotalElements());
            response.put("totalPages", pageTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
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