package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.ItemNotFoundException;
import com.gabo.inventory.models.Item;
import com.gabo.inventory.repositories.ItemRepository;
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
public class ItemController {

    private ItemRepository itemRepository;

    @Autowired
    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }

    @GetMapping(ITEM_PATH)
    public ResponseEntity<List<Item>> getAllItem(@RequestParam(defaultValue = "id,asc") String[] sort) {

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

            List<Item> items = itemRepository.findAll(Sort.by(orders));

            if (items.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(ITEM_PAGE_PATH)
    public ResponseEntity<Map<String, Object>> getPageItems(
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

            List<Item> items = new ArrayList<Item>();
            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

            Page<Item> pageTuts;
            if (name == null)
                pageTuts = itemRepository.findAll(pagingSort);
            else
                pageTuts = itemRepository.findByNameContaining(name, pagingSort);

            items = pageTuts.getContent();

            if (items.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            Map<String, Object> response = new HashMap<>();

            response.put("items", items);
            response.put("currentPage", pageTuts.getNumber());
            response.put("totalItems", pageTuts.getTotalElements());
            response.put("totalPages", pageTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(ITEM_PATH + "/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable("id") String id) {

        Optional<Item> optionalResponse = itemRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new ItemNotFoundException(id);
        }
        return new ResponseEntity<>(optionalResponse.get(), HttpStatus.OK);
    }

    @PostMapping(ITEM_PATH)
    public @ResponseBody ResponseEntity<Item> addItem(@Validated @RequestBody Item item) {
        itemRepository.save(item);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @DeleteMapping(ITEM_PATH + "/{id}")
    public void deleteItem(@PathVariable("id") String id) {

        Optional<Item> optionalResponse = itemRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new ItemNotFoundException(id);
        }
        itemRepository.deleteById(id);
    }

    @PutMapping(ITEM_PATH + "/{id}")
    public ResponseEntity<Item> updateItemById(@Validated @RequestBody Item item, @PathVariable("id") String id) {

        Optional<Item> optionalResponse = itemRepository.findById(id);
        if (!optionalResponse.isPresent()) {

            throw new ItemNotFoundException(id);
        }
        item.id = id;
        itemRepository.save(item);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

}