package com.square.Inventory.Management.System.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class UserDTO {

    private Long id;
    @NotBlank(message = "name is Must")
    private String firstName;

    private String lastName;

    @Pattern(regexp = "^(?:\\+?88|0088)?01[15-9]\\d{8}$", message = "Number Must be valid Number")
    private String contactNumber;
    @Email
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Min(value = 8,message = "Password length must be 8")
    private String password;

    @Pattern(regexp = "^(admin|depot|ssu)$", message = "role will be admin or depot or ssu toForBudget")
    @NotBlank(message = "role is mandatory")
    private String role;
    @Pattern(regexp = "^(active|deactivate)$", message = "role must be active or deactivate")
    private String status;

    private String otp;

    public UserDTO(Long id, String firstName, String lastName,
                   String contactNumber, String email, String role, String status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public UserDTO() {

    }
}
