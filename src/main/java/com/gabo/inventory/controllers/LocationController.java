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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.gabo.inventory.constants.InventoryConstants.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(INVENTORY_V1_PATH)
public class LocationController {

    private LocationRepository locationRepository;

    @Autowired
    public LocationController(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }

    @GetMapping(LOCATION_PATH)
    public ResponseEntity<List<Location>> getAllLocation(@RequestParam(defaultValue = "id,asc") String[] sort) {

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

            List<Location> locations = locationRepository.findAll(Sort.by(orders));

            if (locations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(locations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(LOCATION_PAGE_PATH)
    public ResponseEntity<Map<String, Object>> getPageLocations(
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

            List<Location> locations = new ArrayList<Location>();
            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

            Page<Location> pageTuts;
            if (name == null)
                pageTuts = locationRepository.findAll(pagingSort);
            else
                pageTuts = locationRepository.findByAisleContaining(name, pagingSort);

            locations = pageTuts.getContent();

            if (locations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            Map<String, Object> response = new HashMap<>();

            response.put("locations", locations);
            response.put("currentPage", pageTuts.getNumber());
            response.put("totalLocations", pageTuts.getTotalElements());
            response.put("totalPages", pageTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
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