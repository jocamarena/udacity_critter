package com.udacity.jc.critter.schedule;

import com.udacity.jc.critter.domain.Schedule;
import com.udacity.jc.critter.pet.PetDTO;
import com.udacity.jc.critter.service.DogService;
import com.udacity.jc.critter.service.ScheduleService;
import com.udacity.jc.critter.user.EmployeeDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {
    private ScheduleService scheduleService;
    private DogService dogService;
    public ScheduleController(ScheduleService scheduleService, DogService dogService){
        this.scheduleService = scheduleService;
        this.dogService = dogService;
    }

    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleDTO, schedule);
        scheduleService.saveSchedule(schedule);
        BeanUtils.copyProperties(schedule, scheduleDTO);
        return scheduleDTO;
    }

    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        List<ScheduleDTO> scheduleDTOList = new ArrayList<>();
        List<Schedule> scheduleList = scheduleService.findAllSchedules();
        Iterator<Schedule> itr = scheduleList.iterator();
        while (itr.hasNext()){
            ScheduleDTO scheduleDTO = new ScheduleDTO();
            BeanUtils.copyProperties(itr.next(), scheduleDTO);
            scheduleDTOList.add(scheduleDTO);
        }
        return scheduleDTOList;
    }

    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) {
        List<ScheduleDTO> scheduleDTOList = new ArrayList<>();
        List<Schedule> scheduleList = scheduleService.findScheduleByPetId(petId);
        return getScheduleDTOS(scheduleDTOList, scheduleList);
    }

    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) {
        List<ScheduleDTO> scheduleDTOList = new ArrayList<>();
        List<Schedule> scheduleList = scheduleService.findScheduleByEmployeeId(employeeId);
        return getScheduleDTOS(scheduleDTOList, scheduleList);
        //return scheduleList;
    }

    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) {
        List<PetDTO> petDTOList = dogService.findAllByOwnerId(customerId);
        List<ScheduleDTO> scheduleDTOList = new ArrayList<>();
        List<Schedule> scheduleList = new ArrayList<>();
        Iterator<PetDTO> itr = petDTOList.iterator();
        while (itr.hasNext()){
            scheduleList.addAll(scheduleService.findScheduleByPetId(itr.next().getId()));
        }
        return getScheduleDTOS(scheduleDTOList, scheduleList);
    }
    private List<ScheduleDTO> getScheduleDTOS(List<ScheduleDTO> scheduleDTOList, List<Schedule> scheduleList) {
        Iterator<Schedule> itr = scheduleList.iterator();
        while (itr.hasNext()){
            ScheduleDTO scheduleDTO = new ScheduleDTO();
            Schedule schedule = itr.next();
            BeanUtils.copyProperties(schedule, scheduleDTO);
            scheduleDTOList.add(scheduleDTO);
        }
        return scheduleDTOList;
    }
}
