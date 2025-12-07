package com.v322.healthsync;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import com.v322.healthsync.service.BillingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private BillItemRepository billItemRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private BillingService billingService;

    private Bill testBill;
    private BillItem testBillItem;
    private Patient testPatient;

    @BeforeEach
    void setUp() {
        testPatient = new Patient();
        testPatient.setPersonId("PAT-001");
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");

        testBill = new Bill();
        testBill.setBillId("BILL-001");
        testBill.setPatient(testPatient);
        testBill.setTotalAmount(new BigDecimal("1000.00"));
        testBill.setPaidAmount(BigDecimal.ZERO);
        testBill.setStatus("PENDING");
        testBill.setBillDate(LocalDate.now());

        testBillItem = new BillItem();
        testBillItem.setItemId("ITEM-001");
        testBillItem.setBill(testBill);
        testBillItem.setDescription("Consultation Fee");
        testBillItem.setQuantity(1);
        testBillItem.setTotalPrice(new BigDecimal("500.00"));
    }

    // Generate Bill Tests
    @Test
    void generateBill_Success() {
        when(billRepository.save(any(Bill.class)))
                .thenReturn(testBill);

        Bill result = billingService.generateBill(testBill);

        assertThat(result).isNotNull();
        assertThat(result.getBillId()).startsWith("BILL-");
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getPaidAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getBillDate()).isEqualTo(LocalDate.now());
        verify(billRepository).save(testBill);
    }

    @Test
    void generateBill_SetsCorrectDefaults() {
        when(billRepository.save(any(Bill.class)))
                .thenAnswer(invocation -> {
                    Bill bill = invocation.getArgument(0);
                    assertThat(bill.getStatus()).isEqualTo("PENDING");
                    assertThat(bill.getPaidAmount()).isEqualTo(BigDecimal.ZERO);
                    assertThat(bill.getBillDate()).isNotNull();
                    return bill;
                });

        billingService.generateBill(testBill);

        verify(billRepository).save(testBill);
    }

    // Add Bill Item Tests
    @Test
    void addBillItem_Success() {
        when(billItemRepository.save(any(BillItem.class)))
                .thenReturn(testBillItem);
        when(billItemRepository.findByBillId("BILL-001"))
                .thenReturn(Arrays.asList(testBillItem));
        when(billRepository.findById("BILL-001"))
                .thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class)))
                .thenReturn(testBill);

        BillItem result = billingService.addBillItem(testBillItem);

        assertThat(result).isNotNull();
        assertThat(result.getItemId()).startsWith("ITEM-");
        verify(billItemRepository).save(testBillItem);
        verify(billRepository).save(any(Bill.class));
    }

    @Test
    void addBillItem_UpdatesBillTotal() {
        when(billItemRepository.save(any(BillItem.class)))
                .thenReturn(testBillItem);
        when(billItemRepository.findByBillId("BILL-001"))
                .thenReturn(Arrays.asList(testBillItem));
        when(billRepository.findById("BILL-001"))
                .thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class)))
                .thenReturn(testBill);

        billingService.addBillItem(testBillItem);

        verify(billRepository).save(testBill);
    }

    // Generate Bill With Items Tests
    @Test
    void generateBillWithItems_Success() {
        BillItem item1 = new BillItem();
        item1.setTotalPrice(new BigDecimal("300.00"));
        
        BillItem item2 = new BillItem();
        item2.setTotalPrice(new BigDecimal("200.00"));

        List<BillItem> items = Arrays.asList(item1, item2);

        when(billRepository.save(any(Bill.class)))
                .thenReturn(testBill);
        when(billItemRepository.save(any(BillItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Bill result = billingService.generateBillWithItems(testBill, items);

        assertThat(result).isNotNull();
        assertThat(result.getTotalAmount()).isEqualTo(new BigDecimal("500.00"));
        verify(billItemRepository, times(2)).save(any(BillItem.class));
        verify(billRepository, times(2)).save(any(Bill.class));
    }

    @Test
    void generateBillWithItems_EmptyItems_Success() {
        when(billRepository.save(any(Bill.class)))
                .thenReturn(testBill);

        Bill result = billingService.generateBillWithItems(testBill, Collections.emptyList());

        assertThat(result).isNotNull();
        assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.ZERO);
        verify(billItemRepository, never()).save(any());
    }

    @Test
    void generateBillWithItems_AssignsItemIds() {
        BillItem item = new BillItem();
        item.setTotalPrice(new BigDecimal("100.00"));

        when(billRepository.save(any(Bill.class)))
                .thenReturn(testBill);
        when(billItemRepository.save(any(BillItem.class)))
                .thenAnswer(invocation -> {
                    BillItem savedItem = invocation.getArgument(0);
                    assertThat(savedItem.getItemId()).startsWith("ITEM-");
                    return savedItem;
                });

        billingService.generateBillWithItems(testBill, Arrays.asList(item));

        verify(billItemRepository).save(any(BillItem.class));
    }

    // Get Bill Tests
    @Test
    void getBillById_Success() {
        when(billRepository.findById("BILL-001"))
                .thenReturn(Optional.of(testBill));

        Bill result = billingService.getBillById("BILL-001");

        assertThat(result).isNotNull();
        assertThat(result.getBillId()).isEqualTo("BILL-001");
    }

    @Test
    void getBillById_NotFound_ThrowsException() {
        when(billRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> billingService.getBillById("BILL-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bill not found");
    }

    @Test
    void getBillsByPatient_Success() {
        List<Bill> bills = Arrays.asList(testBill);
        
        when(billRepository.findByPatientId("PAT-001"))
                .thenReturn(bills);

        List<Bill> result = billingService.getBillsByPatient("PAT-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testBill);
    }

    @Test
    void getBillsByPatient_NoBills_ReturnsEmptyList() {
        when(billRepository.findByPatientId(anyString()))
                .thenReturn(Collections.emptyList());

        List<Bill> result = billingService.getBillsByPatient("PAT-999");

        assertThat(result).isEmpty();
    }

    @Test
    void getBillsByStatus_Success() {
        List<Bill> bills = Arrays.asList(testBill);
        
        when(billRepository.findByStatus("PENDING"))
                .thenReturn(bills);

        List<Bill> result = billingService.getBillsByStatus("PENDING");

        assertThat(result).hasSize(1);
    }

    @Test
    void getBillItems_Success() {
        List<BillItem> items = Arrays.asList(testBillItem);
        
        when(billItemRepository.findByBillId("BILL-001"))
                .thenReturn(items);

        List<BillItem> result = billingService.getBillItems("BILL-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testBillItem);
    }

    @Test
    void getUnpaidBills_Success() {
        List<Bill> unpaidBills = Arrays.asList(testBill);
        
        when(billRepository.findUnpaidBills())
                .thenReturn(unpaidBills);

        List<Bill> result = billingService.getUnpaidBills();

        assertThat(result).hasSize(1);
    }

    @Test
    void getUnpaidBillsByPatient_Success() {
        List<Bill> unpaidBills = Arrays.asList(testBill);
        
        when(billRepository.findUnpaidBillsByPatient("PAT-001"))
                .thenReturn(unpaidBills);

        List<Bill> result = billingService.getUnpaidBillsByPatient("PAT-001");

        assertThat(result).hasSize(1);
    }

    @Test
    void getTotalAmountByPatient_Success() {
        when(billRepository.calculateTotalAmountByPatient("PAT-001"))
                .thenReturn(new BigDecimal("5000.00"));

        BigDecimal result = billingService.getTotalAmountByPatient("PAT-001");

        assertThat(result).isEqualTo(new BigDecimal("5000.00"));
    }

    @Test
    void getTotalAmountByPatient_NullResult_ReturnsZero() {
        when(billRepository.calculateTotalAmountByPatient(anyString()))
                .thenReturn(null);

        BigDecimal result = billingService.getTotalAmountByPatient("PAT-999");

        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    // Process Payment Tests
    @Test
    void processPayment_PartialPayment_Success() {
        testBill.setTotalAmount(new BigDecimal("1000.00"));
        testBill.setPaidAmount(BigDecimal.ZERO);

        when(billRepository.findById("BILL-001"))
                .thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class)))
                .thenReturn(testBill);

        Bill result = billingService.processPayment("BILL-001", new BigDecimal("500.00"));

        assertThat(result.getPaidAmount()).isEqualTo(new BigDecimal("500.00"));
        assertThat(result.getStatus()).isEqualTo("PARTIAL");
        verify(billRepository).save(testBill);
    }

    @Test
    void processPayment_FullPayment_StatusPaid() {
        testBill.setTotalAmount(new BigDecimal("1000.00"));
        testBill.setPaidAmount(BigDecimal.ZERO);

        when(billRepository.findById("BILL-001"))
                .thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class)))
                .thenReturn(testBill);

        Bill result = billingService.processPayment("BILL-001", new BigDecimal("1000.00"));

        assertThat(result.getPaidAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(result.getStatus()).isEqualTo("PAID");
        verify(billRepository).save(testBill);
    }

    @Test
    void processPayment_Overpayment_StatusPaid() {
        testBill.setTotalAmount(new BigDecimal("1000.00"));
        testBill.setPaidAmount(BigDecimal.ZERO);

        when(billRepository.findById("BILL-001"))
                .thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class)))
                .thenReturn(testBill);

        Bill result = billingService.processPayment("BILL-001", new BigDecimal("1500.00"));

        assertThat(result.getPaidAmount()).isEqualTo(new BigDecimal("1500.00"));
        assertThat(result.getStatus()).isEqualTo("PAID");
    }

    @Test
    void processPayment_MultiplePayments_Accumulates() {
        testBill.setTotalAmount(new BigDecimal("1000.00"));
        testBill.setPaidAmount(new BigDecimal("300.00"));

        when(billRepository.findById("BILL-001"))
                .thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class)))
                .thenReturn(testBill);

        Bill result = billingService.processPayment("BILL-001", new BigDecimal("200.00"));

        assertThat(result.getPaidAmount()).isEqualTo(new BigDecimal("500.00"));
        assertThat(result.getStatus()).isEqualTo("PARTIAL");
    }

    @Test
    void processPayment_BillNotFound_ThrowsException() {
        when(billRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> 
                billingService.processPayment("BILL-999", new BigDecimal("100.00")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bill not found");
    }

    // Update Bill Tests
    @Test
    void updateBill_UpdateStatus_Success() {
        Bill updateData = new Bill();
        updateData.setStatus("PAID");

        when(billRepository.findById("BILL-001"))
                .thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class)))
                .thenReturn(testBill);

        Bill result = billingService.updateBill("BILL-001", updateData);

        assertThat(result).isNotNull();
        verify(billRepository).save(testBill);
    }

    @Test
    void updateBill_UpdatePaidAmount_UpdatesStatus() {
        testBill.setTotalAmount(new BigDecimal("1000.00"));
        testBill.setPaidAmount(BigDecimal.ZERO);
        
        Bill updateData = new Bill();
        updateData.setPaidAmount(new BigDecimal("1000.00"));

        when(billRepository.findById("BILL-001"))
                .thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class)))
                .thenReturn(testBill);

        billingService.updateBill("BILL-001", updateData);

        assertThat(testBill.getStatus()).isEqualTo("PAID");
        verify(billRepository).save(testBill);
    }

    @Test
    void updateBill_PartialPayment_StatusPartial() {
        testBill.setTotalAmount(new BigDecimal("1000.00"));
        testBill.setPaidAmount(BigDecimal.ZERO);
        
        Bill updateData = new Bill();
        updateData.setPaidAmount(new BigDecimal("500.00"));

        when(billRepository.findById("BILL-001"))
                .thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class)))
                .thenReturn(testBill);

        billingService.updateBill("BILL-001", updateData);

        assertThat(testBill.getStatus()).isEqualTo("PARTIAL");
    }

    @Test
    void updateBill_BillNotFound_ThrowsException() {
        when(billRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> billingService.updateBill("BILL-999", new Bill()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bill not found");
    }

    // Update Bill Item Tests
    @Test
    void updateBillItem_Success() {
        BillItem updateData = new BillItem();
        updateData.setDescription("Updated description");
        updateData.setQuantity(2);
        updateData.setTotalPrice(new BigDecimal("600.00"));

        when(billItemRepository.findById("ITEM-001"))
                .thenReturn(Optional.of(testBillItem));
        when(billItemRepository.save(any(BillItem.class)))
                .thenReturn(testBillItem);
        when(billItemRepository.findByBillId("BILL-001"))
                .thenReturn(Arrays.asList(testBillItem));
        when(billRepository.findById("BILL-001"))
                .thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class)))
                .thenReturn(testBill);

        BillItem result = billingService.updateBillItem("ITEM-001", updateData);

        assertThat(result).isNotNull();
        verify(billItemRepository).save(testBillItem);
        verify(billRepository).save(testBill);
    }

    @Test
    void updateBillItem_ItemNotFound_ThrowsException() {
        when(billItemRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> 
                billingService.updateBillItem("ITEM-999", new BillItem()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bill item not found");
    }

    // Delete Bill Item Tests
    @Test
    void deleteBillItem_Success() {
        when(billItemRepository.findById("ITEM-001"))
                .thenReturn(Optional.of(testBillItem));
        doNothing().when(billItemRepository).deleteById("ITEM-001");
        when(billItemRepository.findByBillId("BILL-001"))
                .thenReturn(Collections.emptyList());
        when(billRepository.findById("BILL-001"))
                .thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class)))
                .thenReturn(testBill);

        billingService.deleteBillItem("ITEM-001");

        verify(billItemRepository).deleteById("ITEM-001");
        verify(billRepository).save(testBill);
    }

    @Test
    void deleteBillItem_ItemNotFound_ThrowsException() {
        when(billItemRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> billingService.deleteBillItem("ITEM-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bill item not found");
    }

    // Delete Bill Tests
    @Test
    void deleteBill_Success() {
        doNothing().when(billRepository).deleteById("BILL-001");

        billingService.deleteBill("BILL-001");

        verify(billRepository).deleteById("BILL-001");
    }

    // Get Bills By Date Range Tests
    @Test
    void getBillsByDateRange_Success() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        List<Bill> bills = Arrays.asList(testBill);

        when(billRepository.findByDateRange(startDate, endDate))
                .thenReturn(bills);

        List<Bill> result = billingService.getBillsByDateRange(startDate, endDate);

        assertThat(result).hasSize(1);
        verify(billRepository).findByDateRange(startDate, endDate);
    }

    @Test
    void getBillsByDateRange_NoBills_ReturnsEmptyList() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        when(billRepository.findByDateRange(startDate, endDate))
                .thenReturn(Collections.emptyList());

        List<Bill> result = billingService.getBillsByDateRange(startDate, endDate);

        assertThat(result).isEmpty();
    }
}