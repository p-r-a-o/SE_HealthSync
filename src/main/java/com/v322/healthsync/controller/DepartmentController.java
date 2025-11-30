package com.v322.healthsync.controller;

import com.v322.healthsync.dto.*;
import com.v322.healthsync.entity.Department;
import com.v322.healthsync.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EntityMapper entityMapper;

    @PostMapping
    public ResponseEntity<DepartmentDTO> createDepartment(@RequestBody DepartmentDTO department) {
        try {
            Department createdDepartment = departmentService.createDepartment(entityMapper.toDepartmentEntity(department));
            return new ResponseEntity<>(DTOMapper.toDepartmentDTO(createdDepartment), HttpStatus.CREATED);  
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable String departmentId) {
        try {
            Department department = departmentService.getDepartmentById(departmentId);
            return new ResponseEntity<>(DTOMapper.toDepartmentDTO(department), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<DepartmentDTO> getDepartmentByName(@PathVariable String name) {
        try {
            Department department = departmentService.getDepartmentByName(name);
            return new ResponseEntity<>(DTOMapper.toDepartmentDTO(department), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        try {
            List<Department> departments = departmentService.getAllDepartments();
            List<DepartmentDTO> responseList = departments.stream().map(DTOMapper::toDepartmentDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<DepartmentDTO>> getDepartmentsByLocation(@PathVariable String location) {
        try {
            List<Department> departments = departmentService.getDepartmentsByLocation(location);
            List<DepartmentDTO> responseList = departments.stream().map(DTOMapper::toDepartmentDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<DepartmentDTO>> searchDepartmentsByName(@RequestParam String keyword) {
        try {
            List<Department> departments = departmentService.searchDepartmentsByName(keyword);
            List<DepartmentDTO> responseList = departments.stream().map(DTOMapper::toDepartmentDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{departmentId}")
    public ResponseEntity<DepartmentDTO> updateDepartment(
            @PathVariable String departmentId,
            @RequestBody DepartmentDTO department) {
        try {
            Department updatedDepartment = departmentService.updateDepartment(departmentId, entityMapper.toDepartmentEntity(department));
            return new ResponseEntity<>(DTOMapper.toDepartmentDTO(updatedDepartment), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<String> deleteDepartment(@PathVariable String departmentId) {
        try {
            departmentService.deleteDepartment(departmentId);
            return new ResponseEntity<>("Department deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete department", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}