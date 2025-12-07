package com.v322.healthsync;

import com.v322.healthsync.entity.Department;
import com.v322.healthsync.repository.DepartmentRepository;
import com.v322.healthsync.service.DepartmentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private Department testDepartment;

    @BeforeEach
    void setUp() {
        testDepartment = new Department();
        testDepartment.setDepartmentId("DEPT-001");
        testDepartment.setName("Cardiology");
        testDepartment.setLocation("Building A, Floor 2");
    }

    // Create Department Tests
    @Test
    void createDepartment_Success() {
        when(departmentRepository.save(any(Department.class)))
                .thenReturn(testDepartment);

        Department result = departmentService.createDepartment(testDepartment);

        assertThat(result).isNotNull();
        assertThat(result.getDepartmentId()).startsWith("DEPT-");
        verify(departmentRepository).save(testDepartment);
    }

    @Test
    void createDepartment_GeneratesUniqueDepartmentId() {
        when(departmentRepository.save(any(Department.class)))
                .thenReturn(testDepartment);

        Department result = departmentService.createDepartment(testDepartment);

        assertThat(result.getDepartmentId()).matches("DEPT-[a-f0-9-]+");
    }

    @Test
    void createDepartment_WithLongName_Success() {
        testDepartment.setName("Department of Advanced Cardiovascular Surgery and Research");
        
        when(departmentRepository.save(any(Department.class)))
                .thenReturn(testDepartment);

        Department result = departmentService.createDepartment(testDepartment);

        assertThat(result).isNotNull();
        verify(departmentRepository).save(testDepartment);
    }

    // Get Department By ID Tests
    @Test
    void getDepartmentById_Success() {
        when(departmentRepository.findById("DEPT-001"))
                .thenReturn(Optional.of(testDepartment));

        Department result = departmentService.getDepartmentById("DEPT-001");

        assertThat(result).isNotNull();
        assertThat(result.getDepartmentId()).isEqualTo("DEPT-001");
        assertThat(result.getName()).isEqualTo("Cardiology");
    }

    @Test
    void getDepartmentById_NotFound_ThrowsException() {
        when(departmentRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentById("DEPT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Department not found");
    }

    @Test
    void getDepartmentById_NullId_ThrowsException() {
        when(departmentRepository.findById(null))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentById(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Department not found");
    }

    // Get Department By Name Tests
    @Test
    void getDepartmentByName_Success() {
        when(departmentRepository.findByName("Cardiology"))
                .thenReturn(Optional.of(testDepartment));

        Department result = departmentService.getDepartmentByName("Cardiology");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Cardiology");
    }

    @Test
    void getDepartmentByName_NotFound_ThrowsException() {
        when(departmentRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentByName("NonExistent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Department not found");
    }

    @Test
    void getDepartmentByName_CaseSensitive_Success() {
        when(departmentRepository.findByName("cardiology"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentByName("cardiology"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Department not found");
    }

    // Get All Departments Tests
    @Test
    void getAllDepartments_Success() {
        Department dept2 = new Department();
        dept2.setDepartmentId("DEPT-002");
        dept2.setName("Neurology");

        List<Department> departments = Arrays.asList(testDepartment, dept2);
        when(departmentRepository.findAll())
                .thenReturn(departments);

        List<Department> result = departmentService.getAllDepartments();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(testDepartment, dept2);
    }

    @Test
    void getAllDepartments_NoDepartments_ReturnsEmptyList() {
        when(departmentRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<Department> result = departmentService.getAllDepartments();

        assertThat(result).isEmpty();
    }

    @Test
    void getAllDepartments_SingleDepartment_Success() {
        when(departmentRepository.findAll())
                .thenReturn(Collections.singletonList(testDepartment));

        List<Department> result = departmentService.getAllDepartments();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testDepartment);
    }

    // Get Departments By Location Tests
    @Test
    void getDepartmentsByLocation_Success() {
        Department dept2 = new Department();
        dept2.setLocation("Building A, Floor 2");

        List<Department> departments = Arrays.asList(testDepartment, dept2);
        when(departmentRepository.findByLocation("Building A, Floor 2"))
                .thenReturn(departments);

        List<Department> result = departmentService.getDepartmentsByLocation("Building A, Floor 2");

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(testDepartment, dept2);
    }

    @Test
    void getDepartmentsByLocation_NoMatch_ReturnsEmptyList() {
        when(departmentRepository.findByLocation(anyString()))
                .thenReturn(Collections.emptyList());

        List<Department> result = departmentService.getDepartmentsByLocation("Building Z");

        assertThat(result).isEmpty();
    }

    @Test
    void getDepartmentsByLocation_PartialMatch_Success() {
        when(departmentRepository.findByLocation("Building A"))
                .thenReturn(Collections.singletonList(testDepartment));

        List<Department> result = departmentService.getDepartmentsByLocation("Building A");

        assertThat(result).hasSize(1);
    }

    // Search Departments By Name Tests
    @Test
    void searchDepartmentsByName_Success() {
        Department dept2 = new Department();
        dept2.setName("Cardiac Surgery");

        List<Department> departments = Arrays.asList(testDepartment, dept2);
        when(departmentRepository.searchByName("card"))
                .thenReturn(departments);

        List<Department> result = departmentService.searchDepartmentsByName("card");

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(testDepartment, dept2);
    }

    @Test
    void searchDepartmentsByName_NoMatch_ReturnsEmptyList() {
        when(departmentRepository.searchByName(anyString()))
                .thenReturn(Collections.emptyList());

        List<Department> result = departmentService.searchDepartmentsByName("xyz");

        assertThat(result).isEmpty();
    }

    @Test
    void searchDepartmentsByName_ExactMatch_Success() {
        when(departmentRepository.searchByName("Cardiology"))
                .thenReturn(Collections.singletonList(testDepartment));

        List<Department> result = departmentService.searchDepartmentsByName("Cardiology");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Cardiology");
    }

    @Test
    void searchDepartmentsByName_EmptyKeyword_ReturnsResults() {
        when(departmentRepository.searchByName(""))
                .thenReturn(Collections.singletonList(testDepartment));

        List<Department> result = departmentService.searchDepartmentsByName("");

        assertThat(result).hasSize(1);
    }

    // Update Department Tests
    @Test
    void updateDepartment_UpdateName_Success() {
        Department updateData = new Department();
        updateData.setName("Updated Cardiology");

        when(departmentRepository.findById("DEPT-001"))
                .thenReturn(Optional.of(testDepartment));
        when(departmentRepository.save(any(Department.class)))
                .thenReturn(testDepartment);

        Department result = departmentService.updateDepartment("DEPT-001", updateData);

        assertThat(result).isNotNull();
        verify(departmentRepository).save(testDepartment);
    }

    @Test
    void updateDepartment_UpdateLocation_Success() {
        Department updateData = new Department();
        updateData.setLocation("Building B, Floor 3");

        when(departmentRepository.findById("DEPT-001"))
                .thenReturn(Optional.of(testDepartment));
        when(departmentRepository.save(any(Department.class)))
                .thenReturn(testDepartment);

        Department result = departmentService.updateDepartment("DEPT-001", updateData);

        assertThat(result).isNotNull();
        verify(departmentRepository).save(testDepartment);
    }

    @Test
    void updateDepartment_UpdateBothFields_Success() {
        Department updateData = new Department();
        updateData.setName("Advanced Cardiology");
        updateData.setLocation("Building C, Floor 1");

        when(departmentRepository.findById("DEPT-001"))
                .thenReturn(Optional.of(testDepartment));
        when(departmentRepository.save(any(Department.class)))
                .thenReturn(testDepartment);

        Department result = departmentService.updateDepartment("DEPT-001", updateData);

        assertThat(result).isNotNull();
        verify(departmentRepository).save(testDepartment);
    }

    @Test
    void updateDepartment_NullFields_DoesNotUpdate() {
        Department updateData = new Department();

        when(departmentRepository.findById("DEPT-001"))
                .thenReturn(Optional.of(testDepartment));
        when(departmentRepository.save(any(Department.class)))
                .thenReturn(testDepartment);

        Department result = departmentService.updateDepartment("DEPT-001", updateData);

        assertThat(result).isNotNull();
        verify(departmentRepository).save(testDepartment);
    }

    @Test
    void updateDepartment_DepartmentNotFound_ThrowsException() {
        when(departmentRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> 
                departmentService.updateDepartment("DEPT-999", new Department()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Department not found");

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void updateDepartment_EmptyName_Success() {
        Department updateData = new Department();
        updateData.setName("");

        when(departmentRepository.findById("DEPT-001"))
                .thenReturn(Optional.of(testDepartment));
        when(departmentRepository.save(any(Department.class)))
                .thenReturn(testDepartment);

        Department result = departmentService.updateDepartment("DEPT-001", updateData);

        assertThat(result).isNotNull();
        verify(departmentRepository).save(testDepartment);
    }

    // Delete Department Tests
    @Test
    void deleteDepartment_Success() {
        doNothing().when(departmentRepository).deleteById("DEPT-001");

        departmentService.deleteDepartment("DEPT-001");

        verify(departmentRepository).deleteById("DEPT-001");
    }

    @Test
    void deleteDepartment_NonExistent_NoException() {
        doNothing().when(departmentRepository).deleteById("DEPT-999");

        departmentService.deleteDepartment("DEPT-999");

        verify(departmentRepository).deleteById("DEPT-999");
    }

    @Test
    void deleteDepartment_NullId_InvokesDelete() {
        doNothing().when(departmentRepository).deleteById(null);

        departmentService.deleteDepartment(null);

        verify(departmentRepository).deleteById(null);
    }

    @Test
    void deleteDepartment_MultipleDeletes_Success() {
        doNothing().when(departmentRepository).deleteById(anyString());

        departmentService.deleteDepartment("DEPT-001");
        departmentService.deleteDepartment("DEPT-002");
        departmentService.deleteDepartment("DEPT-003");

        verify(departmentRepository, times(3)).deleteById(anyString());
    }
}