package com.v322.healthsync;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import com.v322.healthsync.service.BillingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class BillingServiceTest extends BaseIntegrationTest {

    @Autowired
    BillRepository billRepository;

    @Autowired
    BillItemRepository billItemRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    BillingService billingService;

    private Bill testBill;
    private BillItem testBillItem;
    private Patient testPatient;

    @BeforeEach
    void setUp() {
        billItemRepository.deleteAll();
        billRepository.deleteAll();
        patientRepository.deleteAll();

        testPatient = new Patient();
        testPatient.setPersonId("PAT-001");
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");
        testPatient.setEmail("john.doe@test.com");
        testPatient.setPassword("password");
        testPatient = patientRepository.save(testPatient);

        testBill = new Bill();
        testBill.setPatient(testPatient);
        testBill.setTotalAmount(new BigDecimal("1000.00"));
        testBill.setPaidAmount(BigDecimal.ZERO);
        testBill.setStatus("PENDING");
        testBill.setBillDate(LocalDate.now());

        testBillItem = new BillItem();
        testBillItem.setDescription("Consultation Fee");
        testBillItem.setQuantity(1);
        testBillItem.setTotalPrice(new BigDecimal("500.00"));
    }

    // Generate Bill Tests
    @Test
    void generateBill_Success() {
        Bill result = billingService.generateBill(testBill);

        assertThat(result).isNotNull();
        assertThat(result.getBillId()).startsWith("BILL-");
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getPaidAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getBillDate()).isEqualTo(LocalDate.now());
        
        Bill saved = billRepository.findById(result.getBillId()).orElse(null);
        assertThat(saved).isNotNull();
    }

    @Test
    void generateBill_SetsCorrectDefaults() {
        Bill result = billingService.generateBill(testBill);

        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getPaidAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getBillDate()).isNotNull();
        assertThat(result.getBillId()).startsWith("BILL-");
    }

    // Add Bill Item Tests
    @Test
    void addBillItem_Success() {
        Bill savedBill = billingService.generateBill(testBill);
        testBillItem.setBill(savedBill);

        BillItem result = billingService.addBillItem(testBillItem);

        assertThat(result).isNotNull();
        assertThat(result.getItemId()).startsWith("ITEM-");
        
        Bill updatedBill = billRepository.findById(savedBill.getBillId()).orElse(null);
        assertThat(updatedBill).isNotNull();
    }

    @Test
    void addBillItem_UpdatesBillTotal() {
        Bill savedBill = billingService.generateBill(testBill);
        testBillItem.setBill(savedBill);

        billingService.addBillItem(testBillItem);

        Bill updatedBill = billRepository.findById(savedBill.getBillId()).orElse(null);
        assertThat(updatedBill.getTotalAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    // Generate Bill With Items Tests
    @Test
    void generateBillWithItems_Success() {
        BillItem item1 = new BillItem();
        item1.setTotalPrice(new BigDecimal("300.00"));
        item1.setDescription("Item 1");
        item1.setQuantity(1);
        
        BillItem item2 = new BillItem();
        item2.setTotalPrice(new BigDecimal("200.00"));
        item2.setDescription("Item 2");
        item2.setQuantity(1);

        List<BillItem> items = Arrays.asList(item1, item2);

        Bill result = billingService.generateBillWithItems(testBill, items);

        assertThat(result).isNotNull();
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
        
        List<BillItem> savedItems = billItemRepository.findByBillId(result.getBillId());
        assertThat(savedItems).hasSize(2);
    }

    @Test
    void generateBillWithItems_EmptyItems_Success() {
        Bill result = billingService.generateBillWithItems(testBill, Collections.emptyList());

        assertThat(result).isNotNull();
        assertThat(result.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void generateBillWithItems_AssignsItemIds() {
        BillItem item = new BillItem();
        item.setTotalPrice(new BigDecimal("100.00"));
        item.setDescription("Test Item");
        item.setQuantity(1);

        Bill result = billingService.generateBillWithItems(testBill, Arrays.asList(item));

        List<BillItem> savedItems = billItemRepository.findByBillId(result.getBillId());
        assertThat(savedItems).hasSize(1);
        assertThat(savedItems.get(0).getItemId()).startsWith("ITEM-");
    }

    // Get Bill Tests
    @Test
    void getBillById_Success() {
        Bill saved = billingService.generateBill(testBill);

        Bill result = billingService.getBillById(saved.getBillId());

        assertThat(result).isNotNull();
        assertThat(result.getBillId()).isEqualTo(saved.getBillId());
    }

    @Test
    void getBillById_NotFound_ThrowsException() {
        assertThatThrownBy(() -> billingService.getBillById("BILL-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bill not found");
    }

    @Test
    void getBillsByPatient_Success() {
        billingService.generateBill(testBill);

        List<Bill> result = billingService.getBillsByPatient(testPatient.getPersonId());

        assertThat(result).hasSize(1);
    }

    @Test
    void getBillsByPatient_NoBills_ReturnsEmptyList() {
        List<Bill> result = billingService.getBillsByPatient("PAT-999");

        assertThat(result).isEmpty();
    }

    @Test
    void getBillsByStatus_Success() {
        billingService.generateBill(testBill);

        List<Bill> result = billingService.getBillsByStatus("PENDING");

        assertThat(result).hasSize(1);
    }

    @Test
    void getBillItems_Success() {
        Bill savedBill = billingService.generateBill(testBill);
        testBillItem.setBill(savedBill);
        billingService.addBillItem(testBillItem);

        List<BillItem> result = billingService.getBillItems(savedBill.getBillId());

        assertThat(result).hasSize(1);
    }

    @Test
    void getUnpaidBills_Success() {
        billingService.generateBill(testBill);

        List<Bill> result = billingService.getUnpaidBills();

        assertThat(result).hasSize(1);
    }

    @Test
    void getUnpaidBillsByPatient_Success() {
        billingService.generateBill(testBill);

        List<Bill> result = billingService.getUnpaidBillsByPatient(testPatient.getPersonId());

        assertThat(result).hasSize(1);
    }

    @Test
    void getTotalAmountByPatient_Success() {
        testBill.setTotalAmount(new BigDecimal("5000.00"));
        billingService.generateBill(testBill);

        BigDecimal result = billingService.getTotalAmountByPatient(testPatient.getPersonId());

        assertThat(result).isEqualByComparingTo(new BigDecimal("5000.00"));
    }

    @Test
    void getTotalAmountByPatient_NullResult_ReturnsZero() {
        BigDecimal result = billingService.getTotalAmountByPatient("PAT-999");

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // Process Payment Tests
    @Test
    void processPayment_PartialPayment_Success() {
        testBill.setTotalAmount(new BigDecimal("1000.00"));
        testBill.setPaidAmount(BigDecimal.ZERO);
        Bill saved = billingService.generateBill(testBill);

        Bill result = billingService.processPayment(saved.getBillId(), new BigDecimal("500.00"));

        assertThat(result.getPaidAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(result.getStatus()).isEqualTo("PARTIAL");
    }

    @Test
    void processPayment_FullPayment_StatusPaid() {
        testBill.setTotalAmount(new BigDecimal("1000.00"));
        testBill.setPaidAmount(BigDecimal.ZERO);
        Bill saved = billingService.generateBill(testBill);

        Bill result = billingService.processPayment(saved.getBillId(), new BigDecimal("1000.00"));

        assertThat(result.getPaidAmount()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(result.getStatus()).isEqualTo("PAID");
    }

    @Test
    void processPayment_Overpayment_StatusPaid() {
        testBill.setTotalAmount(new BigDecimal("1000.00"));
        testBill.setPaidAmount(BigDecimal.ZERO);
        Bill saved = billingService.generateBill(testBill);

        Bill result = billingService.processPayment(saved.getBillId(), new BigDecimal("1500.00"));

        assertThat(result.getPaidAmount()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(result.getStatus()).isEqualTo("PAID");
    }

    @Test
    void processPayment_MultiplePayments_Accumulates() {
        testBill.setTotalAmount(new BigDecimal("1000.00"));
        testBill.setPaidAmount(BigDecimal.ZERO);
        Bill saved = billingService.generateBill(testBill);
        
        billingService.processPayment(saved.getBillId(), new BigDecimal("300.00"));
        Bill result = billingService.processPayment(saved.getBillId(), new BigDecimal("200.00"));

        assertThat(result.getPaidAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(result.getStatus()).isEqualTo("PARTIAL");
    }

    @Test
    void processPayment_BillNotFound_ThrowsException() {
        assertThatThrownBy(() -> 
                billingService.processPayment("BILL-999", new BigDecimal("100.00")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bill not found");
    }

    // Update Bill Tests
    @Test
    void updateBill_UpdateStatus_Success() {
        Bill saved = billingService.generateBill(testBill);
        
        Bill updateData = new Bill();
        updateData.setStatus("PAID");

        Bill result = billingService.updateBill(saved.getBillId(), updateData);

        assertThat(result.getStatus()).isEqualTo("PAID");
    }

    @Test
    void updateBill_UpdatePaidAmount_UpdatesStatus() {
        testBill.setTotalAmount(new BigDecimal("1000.00"));
        testBill.setPaidAmount(BigDecimal.ZERO);
        Bill saved = billingService.generateBill(testBill);
        
        Bill updateData = new Bill();
        updateData.setPaidAmount(new BigDecimal("1000.00"));

        Bill result = billingService.updateBill(saved.getBillId(), updateData);

        assertThat(result.getStatus()).isEqualTo("PAID");
    }

    @Test
    void updateBill_PartialPayment_StatusPartial() {
        testBill.setTotalAmount(new BigDecimal("1000.00"));
        testBill.setPaidAmount(BigDecimal.ZERO);
        Bill saved = billingService.generateBill(testBill);
        
        Bill updateData = new Bill();
        updateData.setPaidAmount(new BigDecimal("500.00"));

        Bill result = billingService.updateBill(saved.getBillId(), updateData);

        assertThat(result.getStatus()).isEqualTo("PARTIAL");
    }

    @Test
    void updateBill_BillNotFound_ThrowsException() {
        assertThatThrownBy(() -> billingService.updateBill("BILL-999", new Bill()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bill not found");
    }

    // Update Bill Item Tests
    @Test
    void updateBillItem_Success() {
        Bill savedBill = billingService.generateBill(testBill);
        testBillItem.setBill(savedBill);
        BillItem savedItem = billingService.addBillItem(testBillItem);
        
        BillItem updateData = new BillItem();
        updateData.setDescription("Updated description");
        updateData.setQuantity(2);
        updateData.setTotalPrice(new BigDecimal("600.00"));

        BillItem result = billingService.updateBillItem(savedItem.getItemId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getQuantity()).isEqualTo(2);
    }

    @Test
    void updateBillItem_ItemNotFound_ThrowsException() {
        assertThatThrownBy(() -> 
                billingService.updateBillItem("ITEM-999", new BillItem()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bill item not found");
    }

    // Delete Bill Item Tests
    @Test
    void deleteBillItem_Success() {
        Bill savedBill = billingService.generateBill(testBill);
        testBillItem.setBill(savedBill);
        BillItem savedItem = billingService.addBillItem(testBillItem);

        billingService.deleteBillItem(savedItem.getItemId());

        assertThat(billItemRepository.findById(savedItem.getItemId())).isEmpty();
    }

    @Test
    void deleteBillItem_ItemNotFound_ThrowsException() {
        assertThatThrownBy(() -> billingService.deleteBillItem("ITEM-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bill item not found");
    }

    // Delete Bill Tests
    @Test
    void deleteBill_Success() {
        Bill saved = billingService.generateBill(testBill);

        billingService.deleteBill(saved.getBillId());

        assertThat(billRepository.findById(saved.getBillId())).isEmpty();
    }

    // Get Bills By Date Range Tests
    @Test
    void getBillsByDateRange_Success() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        billingService.generateBill(testBill);

        List<Bill> result = billingService.getBillsByDateRange(startDate, endDate);

        assertThat(result).hasSize(1);
    }

    @Test
    void getBillsByDateRange_NoBills_ReturnsEmptyList() {
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 12, 31);

        List<Bill> result = billingService.getBillsByDateRange(startDate, endDate);

        assertThat(result).isEmpty();
    }
}
