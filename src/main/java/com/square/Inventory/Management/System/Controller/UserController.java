package com.square.Inventory.Management.System.Controller;

import com.square.Inventory.Management.System.DTO.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = {"http://localhost:4200"},methods ={RequestMethod.POST,RequestMethod.DELETE,RequestMethod.GET,RequestMethod.PUT})
@RequestMapping(path = "/user")
public interface UserController {

    @PostMapping(path = "/login")
    ResponseEntity<?> login(@RequestBody UserDTO userDTO);

    @PostMapping(path = "/create")
    ResponseEntity<?> createUser(@RequestBody UserDTO user, BindingResult bindingResult);

    @PutMapping(path = "/update/{userId}")
    ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO,
                                      @PathVariable Long userId);

    @PutMapping(path = "/disable/{userId}")
    ResponseEntity<?> disableUser(@PathVariable Long userId);

//    @GetMapping(path = "/all")
//    ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int page,
//                                  @RequestParam(defaultValue = "10") int size);
    @GetMapping(path = "/all")
    ResponseEntity<?> getAllUsers();

    @GetMapping(path = "/page")
    ResponseEntity<?> getAllByPaginationBySorting(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "id") String sortBy);

    @PutMapping(path = "/update/role/{userID}")
    ResponseEntity<?> updateUserRole(@RequestBody String role,
                                     @PathVariable Long userID);

    @PutMapping(path = "/update/status/{userID}")
    ResponseEntity<?> updateUserStatus(@RequestBody String status,
                                       @PathVariable Long userID);

    @GetMapping(path = "/claim_details")
    List<String> getClaimDetails();

    @GetMapping(path = "/claim_object")
    Object getClaimFromLogin();

    @PostMapping("/forgetPassword")
    ResponseEntity<?> forgetPassword(@RequestBody UserDTO userDTO);

    @PostMapping("/checkOtpStatus")
    ResponseEntity<?> checkOtpStatus(@RequestBody UserDTO userDTO);

    @PostMapping("/resetPassword")
    ResponseEntity<?> resetPassword(@RequestBody UserDTO userDTO);


}