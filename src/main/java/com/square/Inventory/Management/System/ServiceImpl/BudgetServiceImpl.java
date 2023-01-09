package com.square.Inventory.Management.System.ServiceImpl;

import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.square.Inventory.Management.System.DTO.BudgetSummary;
import com.square.Inventory.Management.System.DTO.CategoryWiseSummary;
import com.square.Inventory.Management.System.DTO.DEPOT;
import com.square.Inventory.Management.System.DTO.SSU;
import com.square.Inventory.Management.System.Entity.Budget;
import com.square.Inventory.Management.System.Entity.User;
import com.square.Inventory.Management.System.ExcelHepler.BudgetExcelDto;
import com.square.Inventory.Management.System.IMSUtils.TimeUtils;
import com.square.Inventory.Management.System.JWT.JWTFilter;
import com.square.Inventory.Management.System.Projection.BudgetMonthWiseSumProjection;
import com.square.Inventory.Management.System.Projection.BudgetSSUSummaryProjection;
import com.square.Inventory.Management.System.Repository.BudgetRepository;
import com.square.Inventory.Management.System.Repository.DepotRepository;
import com.square.Inventory.Management.System.Repository.SampleSectionRepository;
import com.square.Inventory.Management.System.Repository.UserRepository;
import com.square.Inventory.Management.System.Service.BudgetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;

    private final JWTFilter jwtFilter;

    private final UserRepository userRepository;

    private final DepotRepository depotRepository;

    private final SampleSectionRepository sampleSectionRepository;

    public BudgetServiceImpl(BudgetRepository budgetRepository, JWTFilter jwtFilter, UserRepository userRepository, DepotRepository depotRepository, SampleSectionRepository sampleSectionRepository) {
        this.budgetRepository = budgetRepository;
        this.jwtFilter = jwtFilter;
        this.userRepository = userRepository;
        this.depotRepository = depotRepository;
        this.sampleSectionRepository = sampleSectionRepository;
    }

    @Override
    public List<BudgetExcelDto> getAllBudgetFromExcel(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();

        List<BudgetExcelDto> budgets = Poiji.fromExcel(inputStream, PoijiExcelType.XLSX, BudgetExcelDto.class);
        return new ArrayList<BudgetExcelDto>(budgets);
    }

    @Override
    public List<Budget> addBudgetFromExcel(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        List<BudgetExcelDto> BudgetExcelDTO = Poiji.fromExcel(inputStream, PoijiExcelType.XLSX, BudgetExcelDto.class);

        List<Budget> allBudget = new ArrayList<>();
        int len = BudgetExcelDTO.size();

        for (int i = 0; i < len; i++) {
            BudgetExcelDto _BudgetExcelDTO = BudgetExcelDTO.get(i);
            Budget _budget = new Budget();
            _budget.setSapCode(_BudgetExcelDTO.getSapCode());
            _budget.setBudgetID(_BudgetExcelDTO.getBudgetID());
            _budget.setProductName(_BudgetExcelDTO.getProductName());
            _budget.setProductionUnit(_BudgetExcelDTO.getProductionUnit());
            _budget.setPackageSize(_BudgetExcelDTO.getPackageSize());
            _budget.setSbu(_BudgetExcelDTO.getSbu());
            _budget.setFieldColleagueID(_BudgetExcelDTO.getFieldColleagueID());
            _budget.setFieldColleagueName(_BudgetExcelDTO.getFieldColleagueName());
            _budget.setQuantity(_BudgetExcelDTO.getQuantity());
            _budget.setDepotName(_BudgetExcelDTO.getDepotName());
            _budget.setDepotID(_BudgetExcelDTO.getDepotID());
            _budget.setCategory(_BudgetExcelDTO.getCategory());
            _budget.setMonth(_BudgetExcelDTO.getMonth());
            _budget.setYear(_BudgetExcelDTO.getYear());
            _budget.setSsu_id(_BudgetExcelDTO.getSsu_id());
            budgetRepository.save(_budget);
            allBudget.add(_budget);
        }
        return allBudget;
    }

    @Override
    public ResponseEntity<List<SSU>> getBudgetForSSUByName(String ssuName) {

        List<SSU> ssuList = budgetRepository.getBudgetForSSUByName(ssuName, TimeUtils.getCurrentMonth(), TimeUtils.getCurrentYear());

        return new ResponseEntity<>(ssuList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<DEPOT>> getBudgetForDepotByID(String depotID) {

        List<DEPOT> depotList = budgetRepository.getBudgetForDepotByID(depotID, TimeUtils.getCurrentMonth(), TimeUtils.getCurrentYear());

        return new ResponseEntity<>(depotList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Budget>> getAllBudget() {
        List<Budget> budgetList = budgetRepository.findAll();

        return new ResponseEntity<>(budgetList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<BudgetSummary>> getSummary() {
        List<BudgetSummary> budgetSummaryList = budgetRepository.getSummary();

        return new ResponseEntity<>(budgetSummaryList, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<CategoryWiseSummary> getCategoryWiseSummary() {
        CategoryWiseSummary categoryWiseSummaryList = budgetRepository.getCategoryWiseSummary();
        return new ResponseEntity<>(categoryWiseSummaryList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<CategoryWiseSummary>> getCategoryWiseSummaryDepot() {
        List<CategoryWiseSummary> categoryWiseSummaryList = budgetRepository.getCategoryWiseDepotSummary();
        return new ResponseEntity<>(categoryWiseSummaryList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<BudgetSSUSummaryProjection>> getSSUSummary() {
        return new ResponseEntity<>(budgetRepository.getSSUSummary(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<SSU>> getPreviousMonthBudgetByMonthAndYear(String ssu_id, String month, int year) {
        List<SSU> getPreviousMonthBudgetByMonthAndYearList = budgetRepository.getBudgetForSSUByName(ssu_id, month, year);
        return new ResponseEntity<>(getPreviousMonthBudgetByMonthAndYearList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<DEPOT>> getPreviousDepotBudgetByMonthAndYear(String depotID, String month, int year) {
        List<DEPOT> getPreviousDepotBudgetByMonthAndYearList = budgetRepository.getBudgetForDepotByID(depotID, month, year);
        return new ResponseEntity<>(getPreviousDepotBudgetByMonthAndYearList, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<List<DEPOT>> getDepotBudgetWithUser() {
        User user = userRepository.findByEmail(jwtFilter.getCurrentUser());
        List<DEPOT> getUserWiseBudget = budgetRepository.getBudgetForDepotByID(depotRepository.getDepotID(user.getId()),
                TimeUtils.getCurrentMonth(), TimeUtils.getCurrentYear());

        return new ResponseEntity<>(getUserWiseBudget, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<SSU>> getSSUWiseBudget() {
        User user=userRepository.findByEmail(jwtFilter.getCurrentUser());
        List<SSU> getSSUWiseBudget=budgetRepository.getBudgetForSSUByName(sampleSectionRepository.getSSUNameByUID(user.getId()),
                TimeUtils.getCurrentMonth(),TimeUtils.getCurrentYear());
        return new ResponseEntity<>(getSSUWiseBudget,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<BudgetMonthWiseSumProjection>> getMonthWiseSum() {
        return ResponseEntity.ok(budgetRepository.getMonthWiseSum() );
    }
}
