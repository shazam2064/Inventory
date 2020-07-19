package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.LocationNotFoundException;
import com.gabo.inventory.exceptions.LocationPageParameterException;
import com.gabo.inventory.models.Location;
import com.gabo.inventory.repositories.LocationRepository;
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
    public ResponseEntity<List<Location>> getPagedLocationList(
            @RequestParam(value = "locationList", required = false) Set<String> requestedLocationList,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {

        List<Location> locationList;

        if (requestedLocationList == null || requestedLocationList.isEmpty()) {

            locationList = getPagedLocationList(page, size);
        } else {

            locationList = getFilteredLocationList(requestedLocationList);
        }

        return new ResponseEntity<>(locationList, HttpStatus.OK);
    }

    private List<Location> getFilteredLocationList(Set<String> requestedLocationList) {

        List<Location> locationList;
        Optional<List<Location>> optionalList = locationRepository.findByIdList(requestedLocationList);
        if (!optionalList.isPresent()) {

            throw new LocationNotFoundException("");
        }
        locationList = optionalList.get();
        return locationList;
    }

    private List<Location> getPagedLocationList(Integer page, Integer size) {

        if (page == null ^ size == null) {
            throw new LocationPageParameterException();
        }

        List<Location> locationList;
        if (page == null) {

            locationList = locationRepository.findAll();
        } else {

            Pageable pageable = PageRequest.of(page, size);
            Page<Location> requestedPage = locationRepository.findAll(pageable);
            locationList = Lists.newArrayList(requestedPage);
        }
        return locationList;
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
