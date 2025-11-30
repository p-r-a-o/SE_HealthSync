package com.v322.healthsync.controller;

import com.v322.healthsync.dto.*;
import com.v322.healthsync.entity.Bill;
import com.v322.healthsync.entity.BillItem;
import com.v322.healthsync.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @Autowired
    private EntityMapper entityMapper;

    // FR-18: Generate Bill
    @PostMapping
    public ResponseEntity<BillDTO> generateBill(@RequestBody BillDTO bill) {
        try {
            Bill generatedBill = billingService.generateBill(entityMapper.toBillEntity(bill));
            generatedBill.setBillId(null);
            return new ResponseEntity<>(DTOMapper.toBillDTO(generatedBill), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/with-items")
    public ResponseEntity<BillDTO> generateBillWithItems(@RequestBody BillRequest request) {
        try {
            Bill bill = billingService.generateBillWithItems(request.getBill(), request.getItems());
            return new ResponseEntity<>(DTOMapper.toBillDTO(bill), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-19: Add Bill Items
    @PostMapping("/items")
    public ResponseEntity<BillItemDTO> addBillItem(@RequestBody BillItemDTO item) {
        try {
            BillItem createdItem = billingService.addBillItem(entityMapper.toBillItemEntity(item));
            return new ResponseEntity<>(DTOMapper.toBillItemDTO(createdItem), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-20: View Bill Details
    @GetMapping("/{billId}")
    public ResponseEntity<BillDTO> getBillById(@PathVariable String billId) {
        try {
            Bill bill = billingService.getBillById(billId);
            return new ResponseEntity<>(DTOMapper.toBillDTO(bill), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<BillDTO>> getBillsByPatient(@PathVariable String patientId) {
        try {
            List<Bill> bills = billingService.getBillsByPatient(patientId);
            List<BillDTO> responseList = bills.stream().map(DTOMapper::toBillDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BillDTO>> getBillsByStatus(@PathVariable String status) {
        try {
            List<Bill> bills = billingService.getBillsByStatus(status);
            List<BillDTO> responseList = bills.stream().map(DTOMapper::toBillDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{billId}/items")
    public ResponseEntity<List<BillItemDTO>> getBillItems(@PathVariable String billId) {
        try {
            List<BillItem> items = billingService.getBillItems(billId);
            List<BillItemDTO> responseList = items.stream().map(DTOMapper::toBillItemDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<BillDTO>> getUnpaidBills() {
        try {
            List<Bill> bills = billingService.getUnpaidBills();
            List<BillDTO> responseList = bills.stream().map(DTOMapper::toBillDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/unpaid/patient/{patientId}")
    public ResponseEntity<List<BillDTO>> getUnpaidBillsByPatient(@PathVariable String patientId) {
        try {
            List<Bill> bills = billingService.getUnpaidBillsByPatient(patientId);
            List<BillDTO> responseList = bills.stream().map(DTOMapper::toBillDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/total/patient/{patientId}")
    public ResponseEntity<BigDecimal> getTotalAmountByPatient(@PathVariable String patientId) {
        try {
            BigDecimal total = billingService.getTotalAmountByPatient(patientId);
            return new ResponseEntity<>(total, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<BillDTO>> getBillsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Bill> bills = billingService.getBillsByDateRange(startDate, endDate);
            List<BillDTO> responseList = bills.stream().map(DTOMapper::toBillDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Process Payment
    @PostMapping("/{billId}/payment")
    public ResponseEntity<BillDTO> processPayment(
            @PathVariable String billId,
            @RequestParam BigDecimal amount) {
        try {
            Bill bill = billingService.processPayment(billId, amount);
            return new ResponseEntity<>(DTOMapper.toBillDTO(bill), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{billId}")
    public ResponseEntity<BillDTO> updateBill(@PathVariable String billId, @RequestBody BillDTO bill) {
        try {
            Bill updatedBill = billingService.updateBill(billId, entityMapper.toBillEntity(bill));
            return new ResponseEntity<>(DTOMapper.toBillDTO(updatedBill), HttpStatus.OK);   
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<BillItemDTO> updateBillItem(
            @PathVariable String itemId,
            @RequestBody BillItemDTO item) {
        try {
            BillItem updatedItem = billingService.updateBillItem(itemId, entityMapper.toBillItemEntity(item));
            return new ResponseEntity<>(DTOMapper.toBillItemDTO(updatedItem), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{billId}")
    public ResponseEntity<String> deleteBill(@PathVariable String billId) {
        try {
            billingService.deleteBill(billId);
            return new ResponseEntity<>("Bill deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete bill", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<String> deleteBillItem(@PathVariable String itemId) {
        try {
            billingService.deleteBillItem(itemId);
            return new ResponseEntity<>("Bill item deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete bill item", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper class for request body
    public static class BillRequest {
        private Bill bill;
        private List<BillItem> items;

        public Bill getBill() { return bill; }
        public void setBill(Bill bill) { this.bill = bill; }
        public List<BillItem> getItems() { return items; }
        public void setItems(List<BillItem> items) { this.items = items; }
    }
}