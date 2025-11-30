package com.v322.healthsync.controller;

import com.v322.healthsync.dto.*;
import com.v322.healthsync.entity.Receptionist;
import com.v322.healthsync.entity.Pharmacist;
import com.v322.healthsync.service.ReceptionistService;
import com.v322.healthsync.service.PharmacistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/receptionists")
@CrossOrigin(origins = "*")
public class ReceptionistController {

    @Autowired
    private ReceptionistService receptionistService;

    @Autowired
    private EntityMapper entityMapper;

    @PostMapping
    public ResponseEntity<ReceptionistDTO> createReceptionist(@RequestBody ReceptionistDTO receptionist) {
        try {
            Receptionist createdReceptionist = receptionistService.createReceptionist(entityMapper.toReceptionistEntity(receptionist));
            return new ResponseEntity<>(DTOMapper.toReceptionistDTO(createdReceptionist), HttpStatus.CREATED);  
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{receptionistId}")
    public ResponseEntity<ReceptionistDTO> getReceptionistById(@PathVariable String receptionistId) {
        try {
            Receptionist receptionist = receptionistService.getReceptionistById(receptionistId);
            return new ResponseEntity<>(DTOMapper.toReceptionistDTO(receptionist), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ReceptionistDTO> getReceptionistByEmail(@PathVariable String email) {
        try {
            Receptionist receptionist = receptionistService.getReceptionistByEmail(email);
            return new ResponseEntity<>(DTOMapper.toReceptionistDTO(receptionist), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/contact/{contactNumber}")
    public ResponseEntity<ReceptionistDTO> getReceptionistByContactNumber(@PathVariable String contactNumber) {
        try {
            Receptionist receptionist = receptionistService.getReceptionistByContactNumber(contactNumber);
            return new ResponseEntity<>(DTOMapper.toReceptionistDTO(receptionist), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<ReceptionistDTO>> getAllReceptionists() {
        try {
            List<Receptionist> receptionists = receptionistService.getAllReceptionists();
            List<ReceptionistDTO> responseList = receptionists.stream().map(DTOMapper::toReceptionistDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{receptionistId}")
    public ResponseEntity<ReceptionistDTO> updateReceptionist(
            @PathVariable String receptionistId,
            @RequestBody ReceptionistDTO receptionist) {
        try {
            Receptionist updatedReceptionist = receptionistService.updateReceptionist(receptionistId, entityMapper.toReceptionistEntity(receptionist));
            return new ResponseEntity<>(DTOMapper.toReceptionistDTO(updatedReceptionist), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{receptionistId}")
    public ResponseEntity<String> deleteReceptionist(@PathVariable String receptionistId) {
        try {
            receptionistService.deleteReceptionist(receptionistId);
            return new ResponseEntity<>("Receptionist deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete receptionist", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
