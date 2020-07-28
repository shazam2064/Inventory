package com.gabo.inventory.controllers;

import com.gabo.inventory.exceptions.ItemNotFoundException;
import com.gabo.inventory.exceptions.ItemPageParameterException;
import com.gabo.inventory.models.Item;
import com.gabo.inventory.repositories.ItemRepository;
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
import static com.gabo.inventory.constants.InventoryConstants.ITEM_PATH;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(INVENTORY_V1_PATH)
public class ItemController {


    private ItemRepository itemRepository;

    @Autowired
    public ItemController(ItemRepository itemRepository) {

        this.itemRepository = itemRepository;
    }

    @GetMapping(ITEM_PATH)
    public ResponseEntity<List<Item>> getPagedItemList(
            @RequestParam(value = "itemList", required = false) Set<String> requestedItemList,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {

        List<Item> itemList;

        if (requestedItemList == null || requestedItemList.isEmpty()) {

            itemList = getPagedItemList(page, size);
        } else {

            itemList = getFilteredItemList(requestedItemList);
        }

        return new ResponseEntity<>(itemList, HttpStatus.OK);
    }

    private List<Item> getFilteredItemList(Set<String> requestedItemList) {

        List<Item> itemList;
        Optional<List<Item>> optionalList = itemRepository.findByIdList(requestedItemList);
        if (!optionalList.isPresent()) {

            throw new ItemNotFoundException("");
        }
        itemList = optionalList.get();
        return itemList;
    }

    private List<Item> getPagedItemList(Integer page, Integer size) {

        if (page == null ^ size == null) {
            throw new ItemPageParameterException();
        }

        List<Item> itemList;
        if (page == null) {

            itemList = itemRepository.findAll();
        } else {

            Pageable pageable = PageRequest.of(page, size);
            Page<Item> requestedPage = itemRepository.findAll(pageable);
            itemList = Lists.newArrayList(requestedPage);
        }
        return itemList;
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
