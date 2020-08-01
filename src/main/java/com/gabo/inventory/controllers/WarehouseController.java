package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.WarehouseNotFoundException;
import com.gabo.inventory.exceptions.WarehousePageParameterException;
import com.gabo.inventory.models.Warehouse;
import com.gabo.inventory.repositories.WarehouseRepository;
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
import static com.gabo.inventory.constants.InventoryConstants.WAREHOUSE_PATH;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(INVENTORY_V1_PATH)
public class WarehouseController {

    private WarehouseRepository warehouseRepository;

    @Autowired
    public WarehouseController(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }

    @GetMapping(WAREHOUSE_PATH)
    public ResponseEntity<Map<String, Object>> getAllWarehouses(
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

            List<Warehouse> warehouses;
            if (page == null) {
                warehouses = warehouseRepository.findAll();
                responseAll.put("warehouses",warehouses);
                return new ResponseEntity<>(responseAll, HttpStatus.OK);
            }

            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

            Page<Warehouse> pageTuts;
            if (name == null)
                pageTuts = warehouseRepository.findAll(pagingSort);
            else
                pageTuts = warehouseRepository.findByNameContaining(name, pagingSort);

            warehouses = pageTuts.getContent();

            if (warehouses.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            Map<String, Object> response = new HashMap<>();

            response.put("warehouses", warehouses);
            response.put("currentPage", pageTuts.getNumber());
            response.put("totalWarehouses", pageTuts.getTotalElements());
            response.put("totalPages", pageTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(WAREHOUSE_PATH + "/{id}")
    public ResponseEntity<Warehouse> getWarehouseById(@PathVariable("id") String id) {

        Optional<Warehouse> optionalResponse = warehouseRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new WarehouseNotFoundException(id);
        }
        return new ResponseEntity<>(optionalResponse.get(), HttpStatus.OK);
    }

    @PostMapping(WAREHOUSE_PATH)
    public @ResponseBody ResponseEntity<Warehouse> addWarehouse(@Validated @RequestBody Warehouse warehouse) {
        warehouseRepository.save(warehouse);
        return new ResponseEntity<>(warehouse, HttpStatus.OK);
    }

    @DeleteMapping(WAREHOUSE_PATH + "/{id}")
    public void deleteWarehouse(@PathVariable("id") String id) {

        Optional<Warehouse> optionalResponse = warehouseRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new WarehouseNotFoundException(id);
        }
        warehouseRepository.deleteById(id);
    }

    @PutMapping(WAREHOUSE_PATH + "/{id}")
    public ResponseEntity<Warehouse> updateWarehouseById(@Validated @RequestBody Warehouse warehouse, @PathVariable("id") String id) {

        Optional<Warehouse> optionalResponse = warehouseRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new WarehouseNotFoundException(id);
        }
        warehouse.id = id;
        warehouseRepository.save(warehouse);
        return new ResponseEntity<>(warehouse, HttpStatus.OK);
    }

}