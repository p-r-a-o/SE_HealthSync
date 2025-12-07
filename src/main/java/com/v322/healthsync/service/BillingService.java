package com.v322.healthsync.service;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BillingService {

    @Autowired
    private BillRepository billRepository;
    
    @Autowired
    private BillItemRepository billItemRepository;
    
    @Autowired
    private PatientRepository patientRepository;

    // FR-18: Generate Bill
    public Bill generateBill(Bill bill) {
        bill.setBillId("BILL-" + UUID.randomUUID().toString());
        bill.setBillDate(LocalDate.now());
        // bill.setStatus("PENDING");
        // bill.setPaidAmount(BigDecimal.ZERO);
        
        return billRepository.save(bill);
    }

    // FR-19: Add Bill Items
    public BillItem addBillItem(BillItem item) {
        item.setItemId("ITEM-" + UUID.randomUUID().toString());
        BillItem savedItem = billItemRepository.save(item);

        // Update total amount in bill
        Bill bill = item.getBill();
        updateBillTotalAmount(bill.getBillId());

        return savedItem;
    }

    // Generate bill with items
    public Bill generateBillWithItems(Bill bill, List<BillItem> items) {
        Bill savedBill = generateBill(bill);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (BillItem item : items) {
            item.setBill(savedBill);
            item.setItemId("ITEM-" + UUID.randomUUID().toString());
            billItemRepository.save(item);
            totalAmount = totalAmount.add(item.getTotalPrice());
        }
        
        savedBill.setTotalAmount(totalAmount);
        savedBill.setPaidAmount(bill.getPaidAmount());
        return billRepository.save(savedBill);
    }

    // Update bill total amount based on items
    private void updateBillTotalAmount(String billId) {
        List<BillItem> items = billItemRepository.findByBillId(billId);
        
        BigDecimal totalAmount = items.stream()
            .map(BillItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Bill bill = billRepository.findById(billId)
            .orElseThrow(() -> new RuntimeException("Bill not found"));
        
        bill.setTotalAmount(totalAmount);
        billRepository.save(bill);
    }

    // FR-20: View Bill Details
    public Bill getBillById(String billId) {
        return billRepository.findById(billId)
            .orElseThrow(() -> new RuntimeException("Bill not found"));
    }

    public List<Bill> getBillsByPatient(String patientId) {
        return billRepository.findByPatientId(patientId);
    }

    public List<Bill> getBillsByStatus(String status) {
        return billRepository.findByStatus(status);
    }

    public List<BillItem> getBillItems(String billId) {
        return billItemRepository.findByBillId(billId);
    }

    public List<Bill> getUnpaidBills() {
        return billRepository.findUnpaidBills();
    }

    public List<Bill> getUnpaidBillsByPatient(String patientId) {
        return billRepository.findUnpaidBillsByPatient(patientId);
    }

    public BigDecimal getTotalAmountByPatient(String patientId) {
        BigDecimal total = billRepository.calculateTotalAmountByPatient(patientId);
        return total != null ? total : BigDecimal.ZERO;
    }

    // Process payment
    public Bill processPayment(String billId, BigDecimal paymentAmount) {
        Bill bill = getBillById(billId);

        BigDecimal newPaidAmount = bill.getPaidAmount().add(paymentAmount);
        bill.setPaidAmount(newPaidAmount);

        if (newPaidAmount.compareTo(bill.getTotalAmount()) >= 0) {
            bill.setStatus("PAID");
        } else {
            bill.setStatus("PARTIAL");
        }

        return billRepository.save(bill);
    }

    public Bill updateBill(String billId, Bill updatedBill) {
        Bill existingBill = getBillById(billId);

        if (updatedBill.getStatus() != null) {
            existingBill.setStatus(updatedBill.getStatus());
        }
        if (updatedBill.getPaidAmount() != null) {
            existingBill.setPaidAmount(updatedBill.getPaidAmount());
            
            // Update status based on payment
            if (existingBill.getPaidAmount().compareTo(existingBill.getTotalAmount()) >= 0) {
                existingBill.setStatus("PAID");
            } else if (existingBill.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
                existingBill.setStatus("PARTIAL");
            }
        }

        return billRepository.save(existingBill);
    }

    public BillItem updateBillItem(String itemId, BillItem updatedItem) {
        BillItem existingItem = billItemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Bill item not found"));

        if (updatedItem.getDescription() != null) {
            existingItem.setDescription(updatedItem.getDescription());
        }
        if (updatedItem.getQuantity() != null) {
            existingItem.setQuantity(updatedItem.getQuantity());
        }
        if (updatedItem.getTotalPrice() != null) {
            existingItem.setTotalPrice(updatedItem.getTotalPrice());
        }

        BillItem savedItem = billItemRepository.save(existingItem);
        updateBillTotalAmount(existingItem.getBill().getBillId());

        return savedItem;
    }

    public void deleteBillItem(String itemId) {
        BillItem item = billItemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Bill item not found"));
        
        String billId = item.getBill().getBillId();
        billItemRepository.deleteById(itemId);
        updateBillTotalAmount(billId);
    }

    public void deleteBill(String billId) {
        billRepository.deleteById(billId);
    }

    public List<Bill> getBillsByDateRange(LocalDate startDate, LocalDate endDate) {
        return billRepository.findByDateRange(startDate, endDate);
    }
}