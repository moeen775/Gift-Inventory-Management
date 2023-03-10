package com.square.Inventory.Management.System.Entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "invoices")
public class Invoices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String depotName;

    private String warehouseName;

    private Date createAt;

    @Column(columnDefinition = "json")
    private String productDetails;

}
