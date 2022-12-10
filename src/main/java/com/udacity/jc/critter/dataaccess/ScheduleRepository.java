package com.udacity.jc.critter.dataaccess;

import com.udacity.jc.critter.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    public List<Schedule> findSchedulesByEmployeeIds(Long id);
    public List<Schedule> findSchedulesByPetIds(Long id);
}
