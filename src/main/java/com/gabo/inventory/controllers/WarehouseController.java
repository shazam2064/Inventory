package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.WarehouseNotFoundException;
import com.gabo.inventory.models.Warehouse;
import com.gabo.inventory.repositories.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.gabo.inventory.constants.InventoryConstants.WAREHOUSES_PATH;
import static com.gabo.inventory.constants.InventoryConstants.INVENTORY_V1_PATH;

@RestController
@RequestMapping(INVENTORY_V1_PATH)
public class WarehouseController {


        private WarehouseRepository warehouseRepository;

        @Autowired
        public WarehouseController(WarehouseRepository warehouseRepository) {

            this.warehouseRepository = warehouseRepository;
        }

        @GetMapping(WAREHOUSES_PATH)
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

        @GetMapping(WAREHOUSES_PATH + "/{id}")
        public ResponseEntity<Warehouse> getWarehouseById(@PathVariable("id") String id) {

            Optional<Warehouse> optionalResponse = warehouseRepository.findById(id);
            if (!optionalResponse.isPresent()) {

                throw new WarehouseNotFoundException(id);
            }
            return new ResponseEntity<>(optionalResponse.get(), HttpStatus.OK);
        }

}
