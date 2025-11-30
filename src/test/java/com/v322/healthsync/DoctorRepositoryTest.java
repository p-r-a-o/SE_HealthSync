package com.v322.healthsync;

import static org.assertj.core.api.Assertions.assertThat;

import com.v322.healthsync.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;



class DoctorRepositoryTest extends BaseIntegrationTest {

    @Autowired
    DoctorRepository doctorRepository;

    @Test
    void dataInitializerShouldLoadDoctors() {
        long count = doctorRepository.count();
        assertThat(count).isEqualTo(4); // from initializer
    }
}
