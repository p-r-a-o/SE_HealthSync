package com.v322.healthsync.service;

import com.v322.healthsync.entity.Department;
import com.v322.healthsync.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public Department createDepartment(Department department) {
        department.setDepartmentId("DEPT-" + UUID.randomUUID().toString());
        return departmentRepository.save(department);
    }

    public Department getDepartmentById(String departmentId) {
        return departmentRepository.findById(departmentId)
            .orElseThrow(() -> new RuntimeException("Department not found"));
    }

    public Department getDepartmentByName(String name) {
        return departmentRepository.findByName(name)
            .orElseThrow(() -> new RuntimeException("Department not found"));
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public List<Department> getDepartmentsByLocation(String location) {
        return departmentRepository.findByLocation(location);
    }

    public List<Department> searchDepartmentsByName(String keyword) {
        return departmentRepository.searchByName(keyword);
    }

    public Department updateDepartment(String departmentId, Department updatedDepartment) {
        Department existingDepartment = getDepartmentById(departmentId);

        if (updatedDepartment.getName() != null) {
            existingDepartment.setName(updatedDepartment.getName());
        }
        if (updatedDepartment.getLocation() != null) {
            existingDepartment.setLocation(updatedDepartment.getLocation());
        }

        return departmentRepository.save(existingDepartment);
    }

    public void deleteDepartment(String departmentId) {
        departmentRepository.deleteById(departmentId);
    }
}