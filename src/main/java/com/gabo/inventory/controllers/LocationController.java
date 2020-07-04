package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.LocationNotFoundException;
import com.gabo.inventory.models.Location;
import com.gabo.inventory.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.gabo.inventory.constants.InventoryConstants.INVENTORY_V1_PATH;
import static com.gabo.inventory.constants.InventoryConstants.LOCATION_PATH;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(INVENTORY_V1_PATH)
public class LocationController {


    private LocationRepository locationRepository;

    @Autowired
    public LocationController(LocationRepository locationRepository) {

        this.locationRepository = locationRepository;
    }

    @GetMapping(LOCATION_PATH)
    public ResponseEntity<List<Location>> getLocationList(@RequestParam(required = false)
                                                          Set<String> locationIdList) {

        if (locationIdList == null || locationIdList.isEmpty()) {

            return new ResponseEntity<>(locationRepository.findAll(), HttpStatus.OK);

        } else {

            Optional<List<Location>> optionalList = locationRepository.findByIdList(locationIdList);
            if (!optionalList.isPresent()) {

                throw new LocationNotFoundException("");
            }
            return new ResponseEntity<>(optionalList.get(), HttpStatus.OK);
        }
    }

    @GetMapping(LOCATION_PATH + "/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable("id") String id) {

        Optional<Location> optionalResponse = locationRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new LocationNotFoundException(id);
        }
        return new ResponseEntity<>(optionalResponse.get(), HttpStatus.OK);
    }

    @PostMapping(LOCATION_PATH)
    public @ResponseBody ResponseEntity<Location> addLocation(@Validated @RequestBody Location location) {

        locationRepository.save(location);
        return new ResponseEntity<>(location, HttpStatus.OK);
    }


    @DeleteMapping(LOCATION_PATH + "/{id}")
    public void deleteLocation(@PathVariable("id") String id) {

        Optional<Location> optionalResponse = locationRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new LocationNotFoundException(id);
        }
        locationRepository.deleteById(id);
    }

    @PutMapping(LOCATION_PATH + "/{id}")
    public ResponseEntity<Location> updateLocationById(@Validated @RequestBody Location location, @PathVariable("id") String id) {

        Optional<Location> optionalResponse = locationRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new LocationNotFoundException(id);
        }
        location.id = id;
        locationRepository.save(location);
        return new ResponseEntity<>(location, HttpStatus.OK);
    }

}
