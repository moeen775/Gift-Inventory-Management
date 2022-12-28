package com.example.inventory_project.service;

import com.example.inventory_project.DTO.BudgetDTO;
import com.example.inventory_project.entity.Budget;
import com.example.inventory_project.projection.BudgetProjectionInterface;
import com.example.inventory_project.repository.BudgetRepository;
import com.poiji.bind.Poiji;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public List<BudgetDTO> getAllBudgetFromExcel() {
        List<BudgetDTO> budgets = Poiji.fromExcel(new File("sample_budget.xlsx"), BudgetDTO.class);
//        int length = budgets.size();
        return new ArrayList<BudgetDTO>(budgets);
    }

    public List<Budget> addBudgetFromExcel(){
        List<BudgetDTO> budgetDTO = Poiji.fromExcel(new File("sample_budget.xlsx"), BudgetDTO.class);
        List<Budget> allBudget = new ArrayList<>();
        int len = budgetDTO.size();
        for (int i=0; i<len;i++){
            BudgetDTO _budgetDTO = budgetDTO.get(i);
            Budget _budget = new Budget();
            _budget.setSapCode(_budgetDTO.getSapCode());
            _budget.setBudgetID(_budgetDTO.getBudgetID());
            _budget.setProductName(_budgetDTO.getProductName());
            _budget.setProductionUnit(_budgetDTO.getProductionUnit());
            _budget.setPackageSize(_budgetDTO.getPackageSize());
            _budget.setSbu(_budgetDTO.getSbu());
            _budget.setFieldColleagueID(_budgetDTO.getFieldColleagueID());
            _budget.setFieldColleagueName(_budgetDTO.getFieldColleagueName());
            _budget.setQuantity(_budgetDTO.getQuantity());
            _budget.setDepotName(_budgetDTO.getDepotName());
            _budget.setDepotID(_budgetDTO.getDepotID());
            _budget.setCategory(_budgetDTO.getCategory());
            _budget.setMonth(_budgetDTO.getMonth());
            _budget.setYear(_budgetDTO.getYear());
            _budget.setSsu_id(_budgetDTO.getSsu_id());
            budgetRepository.save(_budget);
            allBudget.add(_budget);
        }
        return allBudget;
    }

    public List<BudgetProjectionInterface> showDepotWiseProductSum() {
        return budgetRepository.getDepotWiseProductSum();
    }


}
