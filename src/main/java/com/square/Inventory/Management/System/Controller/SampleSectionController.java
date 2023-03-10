package com.square.Inventory.Management.System.Controller;

import com.square.Inventory.Management.System.DTO.SsuDto;
import com.square.Inventory.Management.System.Entity.SampleSectionUnit;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path = "/ssu")
public interface SampleSectionController {

    @PostMapping(path = "/create")
    ResponseEntity<?> createSSu(@RequestBody SsuDto sampleSectionUnit,
                                BindingResult bindingResult);

    @GetMapping(path = "/get/all")
    ResponseEntity<?> getAllSSU(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size);

    @PutMapping(path = "/edit/{ssuID}")
    ResponseEntity<?> updateSSU(@RequestBody SampleSectionUnit sampleSectionUnit,
                                @PathVariable Long ssuID);

    @DeleteMapping(path = "/delete/{ssuID}")
    ResponseEntity<?> deleteSSU(@PathVariable Long ssuID);
}
