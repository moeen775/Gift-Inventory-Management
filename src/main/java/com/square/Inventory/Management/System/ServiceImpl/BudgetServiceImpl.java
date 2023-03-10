package com.square.Inventory.Management.System.ServiceImpl;

import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.square.Inventory.Management.System.DTO.BudgetSummaryProjection;
import com.square.Inventory.Management.System.DTO.CategoryWiseSummary;
import com.square.Inventory.Management.System.DTO.depotDtoForBudget;
import com.square.Inventory.Management.System.DTO.SSUDtoForBudget;
import com.square.Inventory.Management.System.Entity.Budget;
import com.square.Inventory.Management.System.Entity.SBU;
import com.square.Inventory.Management.System.Entity.User;
import com.square.Inventory.Management.System.ExcelHepler.BudgetExcelDto;
import com.square.Inventory.Management.System.IMSUtils.TimeUtils;
import com.square.Inventory.Management.System.JWT.JWTFilter;
import com.square.Inventory.Management.System.Projection.BudgetMonthWiseSumProjection;
import com.square.Inventory.Management.System.Projection.BudgetSSUSummaryProjection;
import com.square.Inventory.Management.System.Projection.FieldColleagueProjection;
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

import javax.validation.Valid;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> addBudgetFromExcel(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        List<BudgetExcelDto> budgetExcelDto = Poiji.fromExcel(inputStream, PoijiExcelType.XLSX, BudgetExcelDto.class);
        List<BudgetExcelDto> copyBudgetExcelDto = new ArrayList<>(budgetExcelDto);

        budgetExcelDto.removeIf(budgetDto -> !budgetDto.getCategory().toLowerCase().matches("^(sample|ppm|gift)$")
                        || (!budgetDto.getSsu_id().toLowerCase().matches("^(dpg|dso|du)$"))
                        || (!(budgetDto.getYear() == TimeUtils.getCurrentYear()))
                        || (!(
                        budgetDto.getSbu() == SBU.A
                                || budgetDto.getSbu() == SBU.B
                                || budgetDto.getSbu() == SBU.C
                                || budgetDto.getSbu() == SBU.D
                                || budgetDto.getSbu() == SBU.E
                                || budgetDto.getSbu() == SBU.N
                        ))
        );
        copyBudgetExcelDto.removeAll(budgetExcelDto);
        log.info("total {} rows are not included!", copyBudgetExcelDto.size());
//        if (copyBudgetExcelDto.size() != budgetExcelDto.size()) {
//            return new ArrayList<>();
//        }

        for (BudgetExcelDto _budgetExcelDto : budgetExcelDto) {
            Budget _budget = new Budget();
            _budget.setSapCode(_budgetExcelDto.getSapCode());
            _budget.setBudgetId(_budgetExcelDto.getBudgetID());
            _budget.setProductName(_budgetExcelDto.getProductName());
            _budget.setProductionUnit(_budgetExcelDto.getProductionUnit());
            _budget.setPackageSize(_budgetExcelDto.getPackageSize());
            _budget.setSbu(_budgetExcelDto.getSbu());
            _budget.setFieldColleagueId(_budgetExcelDto.getFieldColleagueID());
            _budget.setFieldColleagueName(_budgetExcelDto.getFieldColleagueName());
            _budget.setQuantity(Math.round(_budgetExcelDto.getQuantity()));
            _budget.setDepotName(_budgetExcelDto.getDepotName());
            _budget.setDepotId(_budgetExcelDto.getDepotID());
            _budget.setCategory(_budgetExcelDto.getCategory());
            _budget.setMonth(_budgetExcelDto.getMonth());
            _budget.setYear(_budgetExcelDto.getYear());
            _budget.setSsuId(_budgetExcelDto.getSsu_id());
            budgetRepository.save(_budget);
        }   
        return ResponseEntity.ok().header("message","The " + copyBudgetExcelDto.size()+ " rows are not included").body(copyBudgetExcelDto);
    }

    @Override
    public ResponseEntity<List<SSUDtoForBudget>> getBudgetForSSUByName(String ssuName) {

        List<SSUDtoForBudget> SSUDtoForBudgetList = budgetRepository.getBudgetForSSUByName(ssuName, TimeUtils.getCurrentMonth(), TimeUtils.getCurrentYear());

        return new ResponseEntity<>(SSUDtoForBudgetList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<depotDtoForBudget>> getBudgetForDepotByID(String depotID) {

        List<depotDtoForBudget> depotDtoForBudgetList = budgetRepository.getBudgetForDepotByID(depotID, TimeUtils.getCurrentMonth(), TimeUtils.getCurrentYear());

        return new ResponseEntity<>(depotDtoForBudgetList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Budget>> getAllBudget() {
        List<Budget> budgetList = budgetRepository.findAll();

        return new ResponseEntity<>(budgetList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<BudgetSummaryProjection>> getSummary() {
        List<BudgetSummaryProjection> budgetSummaryProjectionList = budgetRepository.getSummary();

        return new ResponseEntity<>(budgetSummaryProjectionList, HttpStatus.OK);
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
    public ResponseEntity<List<SSUDtoForBudget>> getPreviousMonthBudgetByMonthAndYear(String ssu_id, String month, int year) {
        List<SSUDtoForBudget> getPreviousMonthBudgetByMonthAndYearList = budgetRepository.getBudgetForSSUByName(ssu_id, month, year);
        return new ResponseEntity<>(getPreviousMonthBudgetByMonthAndYearList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<depotDtoForBudget>> getPreviousDepotBudgetByMonthAndYear(String depotID, String month, int year) {
        List<depotDtoForBudget> getPreviousDepotDtoForBudgetBudgetByMonthAndYearList = budgetRepository.getBudgetForDepotByID(depotID, month, year);
        return new ResponseEntity<>(getPreviousDepotDtoForBudgetBudgetByMonthAndYearList, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<List<depotDtoForBudget>> getDepotUserWiseBudget() {
        User user = userRepository.findByEmail(jwtFilter.getCurrentUser());
        List<depotDtoForBudget> getUserWiseBudget = budgetRepository.getBudgetForDepotUser(depotRepository.getDepotID(user.getId()),
                TimeUtils.getCurrentMonth(), TimeUtils.getCurrentYear());

        return new ResponseEntity<>(getUserWiseBudget, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<SSUDtoForBudget>> getSSUUSerWiseBudget() {
        User user = userRepository.findByEmail(jwtFilter.getCurrentUser());
        List<SSUDtoForBudget> getSSUDtoForBudgetWiseBudget = budgetRepository.getBudgetForSSUByName(sampleSectionRepository.getSSUNameByUID(user.getId()),
                TimeUtils.getCurrentMonth(), TimeUtils.getCurrentYear());
        return new ResponseEntity<>(getSSUDtoForBudgetWiseBudget, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<BudgetMonthWiseSumProjection>> getMonthWiseSum() {
        return ResponseEntity.ok(budgetRepository.getMonthWiseSum());
    }

    @Override
    public ResponseEntity<FieldColleagueProjection> getCurrentMonthFieldColleague() {
        String month = TimeUtils.getCurrentMonth();
        return ResponseEntity.ok(budgetRepository.getCurrentMonthFieldColleague(month));
    }
}
