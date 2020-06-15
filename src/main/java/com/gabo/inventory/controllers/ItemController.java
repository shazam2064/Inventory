package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.ItemNotFoundException;
import com.gabo.inventory.models.Item;
import com.gabo.inventory.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.gabo.inventory.constants.InventoryConstants.INVENTORY_V1_PATH;
import static com.gabo.inventory.constants.InventoryConstants.ITEM_PATH;

@RestController
@RequestMapping(INVENTORY_V1_PATH)
public class ItemController {


    private ItemRepository itemRepository;

    @Autowired
    public ItemController(ItemRepository itemRepository) {

        this.itemRepository = itemRepository;
    }

    @GetMapping(ITEM_PATH)
    public ResponseEntity<List<Item>> getItemList(@RequestParam(required = false)
                                                          Set<String> itemIdList) {

        if (itemIdList == null || itemIdList.isEmpty()) {

            return new ResponseEntity<>(itemRepository.findAll(), HttpStatus.OK);

        } else {

            Optional<List<Item>> optionalList = itemRepository.findByIdList(itemIdList);
            if (!optionalList.isPresent()) {

                throw new ItemNotFoundException("");
            }
            return new ResponseEntity<>(optionalList.get(), HttpStatus.OK);
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
