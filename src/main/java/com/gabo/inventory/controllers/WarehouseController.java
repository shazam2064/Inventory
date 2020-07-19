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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @GetMapping(WAREHOUSE_PATH)
    public ResponseEntity<List<Warehouse>> getPagedWarehouseList(
            @RequestParam(value = "warehouseList", required = false) Set<String> requestedWarehouseList,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {

        List<Warehouse> warehouseList;

        if (requestedWarehouseList == null || requestedWarehouseList.isEmpty()) {

            warehouseList = getPagedWarehouseList(page, size);
        } else {

            warehouseList = getFilteredWarehouseList(requestedWarehouseList);
        }

        return new ResponseEntity<>(warehouseList, HttpStatus.OK);
    }

    private List<Warehouse> getFilteredWarehouseList(Set<String> requestedWarehouseList) {

        List<Warehouse> warehouseList;
        Optional<List<Warehouse>> optionalList = warehouseRepository.findByIdList(requestedWarehouseList);
        if (!optionalList.isPresent()) {

            throw new WarehouseNotFoundException("");
        }
        warehouseList = optionalList.get();
        return warehouseList;
    }

    private List<Warehouse> getPagedWarehouseList(Integer page, Integer size) {

        if (page == null ^ size == null) {
            throw new WarehousePageParameterException();
        }

        List<Warehouse> warehouseList;
        if (page == null) {

            warehouseList = warehouseRepository.findAll();
        } else {

            Pageable pageable = PageRequest.of(page, size);
            Page<Warehouse> requestedPage = warehouseRepository.findAll(pageable);
            warehouseList = Lists.newArrayList(requestedPage);
        }
        return warehouseList;
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