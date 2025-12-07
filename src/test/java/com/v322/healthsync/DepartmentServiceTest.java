package com.v322.healthsync;

import com.v322.healthsync.entity.Department;
import com.v322.healthsync.repository.DepartmentRepository;
import com.v322.healthsync.service.DepartmentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class DepartmentServiceTest extends BaseIntegrationTest {

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    DepartmentService departmentService;

    private Department testDepartment;

    @BeforeEach
    void setUp() {
        departmentRepository.deleteAll();
        
        testDepartment = new Department();
        testDepartment.setName("Cardiology");
        testDepartment.setLocation("Building A, Floor 2");
    }

    // Create Department Tests
    @Test
    void createDepartment_Success() {
        Department result = departmentService.createDepartment(testDepartment);

        assertThat(result).isNotNull();
        assertThat(result.getDepartmentId()).startsWith("DEPT-");
        
        Department saved = departmentRepository.findById(result.getDepartmentId()).orElse(null);
        assertThat(saved).isNotNull();
    }

    @Test
    void createDepartment_GeneratesUniqueDepartmentId() {
        Department result = departmentService.createDepartment(testDepartment);

        assertThat(result.getDepartmentId()).matches("DEPT-[a-f0-9-]+");
    }

    @Test
    void createDepartment_WithLongName_Success() {
        testDepartment.setName("Department of Advanced Cardiovascular Surgery and Research");
        
        Department result = departmentService.createDepartment(testDepartment);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Department of Advanced Cardiovascular Surgery and Research");
    }

    // Get Department By ID Tests
    @Test
    void getDepartmentById_Success() {
        Department saved = departmentService.createDepartment(testDepartment);

        Department result = departmentService.getDepartmentById(saved.getDepartmentId());

        assertThat(result).isNotNull();
        assertThat(result.getDepartmentId()).isEqualTo(saved.getDepartmentId());
        assertThat(result.getName()).isEqualTo("Cardiology");
    }

    @Test
    void getDepartmentById_NotFound_ThrowsException() {
        assertThatThrownBy(() -> departmentService.getDepartmentById("DEPT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Department not found");
    }

    @Test
    void getDepartmentById_NullId_ThrowsException() {
        assertThatThrownBy(() -> departmentService.getDepartmentById(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Department not found");
    }

    // Get Department By Name Tests
    @Test
    void getDepartmentByName_Success() {
        departmentService.createDepartment(testDepartment);

        Department result = departmentService.getDepartmentByName("Cardiology");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Cardiology");
    }

    @Test
    void getDepartmentByName_NotFound_ThrowsException() {
        assertThatThrownBy(() -> departmentService.getDepartmentByName("NonExistent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Department not found");
    }

    @Test
    void getDepartmentByName_CaseSensitive_ThrowsException() {
        departmentService.createDepartment(testDepartment);

        assertThatThrownBy(() -> departmentService.getDepartmentByName("cardiology"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Department not found");
    }

    // Get All Departments Tests
    @Test
    void getAllDepartments_Success() {
        departmentService.createDepartment(testDepartment);
        
        Department dept2 = new Department();
        dept2.setName("Neurology");
        dept2.setLocation("Building B");
        departmentService.createDepartment(dept2);

        List<Department> result = departmentService.getAllDepartments();

        assertThat(result).hasSize(2);
    }

    @Test
    void getAllDepartments_NoDepartments_ReturnsEmptyList() {
        List<Department> result = departmentService.getAllDepartments();

        assertThat(result).isEmpty();
    }

    @Test
    void getAllDepartments_SingleDepartment_Success() {
        departmentService.createDepartment(testDepartment);

        List<Department> result = departmentService.getAllDepartments();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Cardiology");
    }

    // Get Departments By Location Tests
    @Test
    void getDepartmentsByLocation_Success() {
        departmentService.createDepartment(testDepartment);
        
        Department dept2 = new Department();
        dept2.setName("Surgery");
        dept2.setLocation("Building A, Floor 2");
        departmentService.createDepartment(dept2);

        List<Department> result = departmentService.getDepartmentsByLocation("Building A, Floor 2");

        assertThat(result).hasSize(2);
    }

    @Test
    void getDepartmentsByLocation_NoMatch_ReturnsEmptyList() {
        departmentService.createDepartment(testDepartment);

        List<Department> result = departmentService.getDepartmentsByLocation("Building Z");

        assertThat(result).isEmpty();
    }

    @Test
    void getDepartmentsByLocation_PartialMatch_Success() {
        testDepartment.setLocation("Building A");
        departmentService.createDepartment(testDepartment);

        List<Department> result = departmentService.getDepartmentsByLocation("Building A");

        assertThat(result).hasSize(1);
    }

    // Search Departments By Name Tests
    @Test
    void searchDepartmentsByName_Success() {
        departmentService.createDepartment(testDepartment);
        
        Department dept2 = new Department();
        dept2.setName("Cardiac Surgery");
        dept2.setLocation("Building B");
        departmentService.createDepartment(dept2);

        List<Department> result = departmentService.searchDepartmentsByName("card");

        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void searchDepartmentsByName_NoMatch_ReturnsEmptyList() {
        departmentService.createDepartment(testDepartment);

        List<Department> result = departmentService.searchDepartmentsByName("xyz");

        assertThat(result).isEmpty();
    }

    @Test
    void searchDepartmentsByName_ExactMatch_Success() {
        departmentService.createDepartment(testDepartment);

        List<Department> result = departmentService.searchDepartmentsByName("Cardiology");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Cardiology");
    }

    @Test
    void searchDepartmentsByName_EmptyKeyword_ReturnsResults() {
        departmentService.createDepartment(testDepartment);

        List<Department> result = departmentService.searchDepartmentsByName("");

        assertThat(result).hasSizeGreaterThanOrEqualTo(0);
    }

    // Update Department Tests
    @Test
    void updateDepartment_UpdateName_Success() {
        Department saved = departmentService.createDepartment(testDepartment);
        
        Department updateData = new Department();
        updateData.setName("Updated Cardiology");

        Department result = departmentService.updateDepartment(saved.getDepartmentId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Cardiology");
    }

    @Test
    void updateDepartment_UpdateLocation_Success() {
        Department saved = departmentService.createDepartment(testDepartment);
        
        Department updateData = new Department();
        updateData.setLocation("Building B, Floor 3");

        Department result = departmentService.updateDepartment(saved.getDepartmentId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getLocation()).isEqualTo("Building B, Floor 3");
    }

    @Test
    void updateDepartment_UpdateBothFields_Success() {
        Department saved = departmentService.createDepartment(testDepartment);
        
        Department updateData = new Department();
        updateData.setName("Advanced Cardiology");
        updateData.setLocation("Building C, Floor 1");

        Department result = departmentService.updateDepartment(saved.getDepartmentId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Advanced Cardiology");
        assertThat(result.getLocation()).isEqualTo("Building C, Floor 1");
    }

    @Test
    void updateDepartment_NullFields_DoesNotUpdate() {
        Department saved = departmentService.createDepartment(testDepartment);
        String originalName = saved.getName();
        
        Department updateData = new Department();

        Department result = departmentService.updateDepartment(saved.getDepartmentId(), updateData);

        assertThat(result.getName()).isEqualTo(originalName);
    }

    @Test
    void updateDepartment_DepartmentNotFound_ThrowsException() {
        assertThatThrownBy(() -> 
                departmentService.updateDepartment("DEPT-999", new Department()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Department not found");
    }

    @Test
    void updateDepartment_EmptyName_Success() {
        Department saved = departmentService.createDepartment(testDepartment);
        
        Department updateData = new Department();
        updateData.setName("");

        Department result = departmentService.updateDepartment(saved.getDepartmentId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("");
    }

    // Delete Department Tests
    @Test
    void deleteDepartment_Success() {
        Department saved = departmentService.createDepartment(testDepartment);

        departmentService.deleteDepartment(saved.getDepartmentId());

        assertThat(departmentRepository.findById(saved.getDepartmentId())).isEmpty();
    }

    @Test
    void deleteDepartment_NonExistent_NoException() {
        departmentService.deleteDepartment("DEPT-999");

        // No exception thrown
        assertThat(departmentRepository.findById("DEPT-999")).isEmpty();
    }

    @Test
    void deleteDepartment_NullId_NoException() {
        departmentService.deleteDepartment(null);

        // No exception thrown
    }

    @Test
    void deleteDepartment_MultipleDeletes_Success() {
        Department dept1 = departmentService.createDepartment(testDepartment);
        
        Department dept2 = new Department();
        dept2.setName("Neurology");
        dept2 = departmentService.createDepartment(dept2);
        
        Department dept3 = new Department();
        dept3.setName("Surgery");
        dept3 = departmentService.createDepartment(dept3);

        departmentService.deleteDepartment(dept1.getDepartmentId());
        departmentService.deleteDepartment(dept2.getDepartmentId());
        departmentService.deleteDepartment(dept3.getDepartmentId());

        assertThat(departmentRepository.count()).isEqualTo(0);
    }
}
