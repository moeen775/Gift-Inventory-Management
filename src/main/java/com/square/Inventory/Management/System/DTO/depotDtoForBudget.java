package com.square.Inventory.Management.System.DTO;

import com.square.Inventory.Management.System.Entity.SBU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class depotDtoForBudget {

    private String budgetId;

    private String sapCode;

    private String productName;

    private String productionUnit;

    private String packageSize;

    private String category;

    private SBU sbu;

    private String fieldColleagueId;

    private String fieldColleagueName;

    private int quantity;

    private String month;

    private int year;
}
