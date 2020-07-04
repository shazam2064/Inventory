package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.WarehouseNotFoundException;
import com.gabo.inventory.models.Warehouse;
import com.gabo.inventory.repositories.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<Warehouse>> getWarehouseList(@RequestParam(required = false)
                                                                    Set<String> warehouseIdList) {

        if (warehouseIdList == null || warehouseIdList.isEmpty()) {

            return new ResponseEntity<>(warehouseRepository.findAll(), HttpStatus.OK);

        } else {

            Optional<List<Warehouse>> optionalList = warehouseRepository.findByIdList(warehouseIdList);
            if (!optionalList.isPresent()) {

                throw new WarehouseNotFoundException("");
            }
            return new ResponseEntity<>(optionalList.get(), HttpStatus.OK);
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
