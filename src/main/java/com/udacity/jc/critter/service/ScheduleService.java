package com.udacity.jc.critter.service;

import com.udacity.jc.critter.dataaccess.ScheduleRepository;
import com.udacity.jc.critter.domain.Schedule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ScheduleService {
    private ScheduleRepository scheduleRepository;
    public ScheduleService(ScheduleRepository scheduleRepository){
        this.scheduleRepository = scheduleRepository;
    }
    public List<Schedule> findAllSchedules(){
        return scheduleRepository.findAll();
    }
    public Long saveSchedule(Schedule schedule){
        return scheduleRepository.save(schedule).getId();
    }
    public List<Schedule> findScheduleByEmployeeId(Long id){
        return scheduleRepository.findSchedulesByEmployeeIds(id);
    }
    public List<Schedule> findScheduleByPetId(Long id){
        return scheduleRepository.findSchedulesByPetIds(id);
    }
}
