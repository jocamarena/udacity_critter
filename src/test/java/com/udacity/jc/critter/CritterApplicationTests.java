package com.udacity.jc.critter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.udacity.jc.critter.domain.Customer;
import com.udacity.jc.critter.pet.PetController;
import com.udacity.jc.critter.pet.PetDTO;
import com.udacity.jc.critter.pet.PetType;
import com.udacity.jc.critter.schedule.ScheduleController;
import com.udacity.jc.critter.schedule.ScheduleDTO;
import com.udacity.jc.critter.user.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Transactional
@SpringBootTest(classes = CritterApplication.class)
class CritterApplicationTests {

	@Autowired
	private UserController userController;

	@Autowired
	private PetController petController;

	@Autowired
	private ScheduleController scheduleController;

	@Test
	public void mytest(){
		Iterable<Customer> iCustomers = userController.findAllCustomers();
		Assertions.assertNotNull(iCustomers);
	}
	@Test
	public void testCreateCustomer(){
		CustomerDTO customerDTO = createCustomerDTO();
		CustomerDTO newCustomer = userController.saveCustomer(customerDTO);
		System.out.println("newCustomer.id " + newCustomer.getId());
		//CustomerDTO retrievedCustomer = userController.findAllCustomers().iterator().
		Assertions.assertNotNull(newCustomer);
		Assertions.assertEquals(newCustomer.getName(), customerDTO.getName());
		Assertions.assertNotNull(newCustomer.getId());
		Assertions.assertTrue(userController.findCustomerById(newCustomer.getId()).get().getId() > 0L);
		Assertions.assertEquals(newCustomer.getId(), userController.findCustomerById(newCustomer.getId()).get().getId());
		//Assertions.assertTrue(newCustomer.getId() > 0);
	}

	@Test
	public void testCreateEmployee(){
		EmployeeDTO employeeDTO = createEmployeeDTO();
		EmployeeDTO newEmployee = userController.saveEmployee(employeeDTO);
		EmployeeDTO retrievedEmployee = userController.getEmployee(newEmployee.getId());
		Assertions.assertNotNull(newEmployee.getId());
		Assertions.assertTrue(newEmployee.getId() > 0L);
		Assertions.assertEquals(employeeDTO.getSkills(), newEmployee.getSkills());
		Assertions.assertEquals(newEmployee.getId(), retrievedEmployee.getId());
		Assertions.assertTrue(retrievedEmployee.getId() > 0);
	}

	@Test
	public void testAddPetsToCustomer() {
		CustomerDTO customerDTO = createCustomerDTO();
		CustomerDTO newCustomer = userController.saveCustomer(customerDTO);
		Assertions.assertTrue(newCustomer.getId() > 0L);
		Assertions.assertEquals(newCustomer.getId(), userController.findCustomerById(newCustomer.getId()).get().getId());
		PetDTO petDTO = createPetDTO();
		petDTO.setOwnerId(newCustomer.getId());
		Assertions.assertEquals(petDTO.getOwnerId(), newCustomer.getId());
		PetDTO newPet = petController.savePet(petDTO);
		Assertions.assertTrue(newPet.getId() > 0L);
		Assertions.assertEquals(newPet.getOwnerId(), newCustomer.getId());
		Assertions.assertTrue(newPet.getOwnerId() > 0L);
		//make sure pet contains customer id
		PetDTO retrievedPet = petController.getPet(newPet.getId());
		Assertions.assertTrue(retrievedPet.getId() > 0L);
		Assertions.assertEquals(retrievedPet.getId(), newPet.getId());
		Assertions.assertTrue(retrievedPet.getOwnerId() > 0L);
		Assertions.assertEquals(retrievedPet.getOwnerId(), newCustomer.getId());
		Assertions.assertNotNull(retrievedPet.getOwnerId());
		Assertions.assertTrue(retrievedPet.getOwnerId() > 0L);
		//make sure you can retrieve pets by owner
		List<PetDTO> pets = petController.getPetsByOwner(newCustomer.getId());
		Assertions.assertEquals(newPet.getId(), pets.get(0).getId());
		Assertions.assertEquals(newPet.getName(), pets.get(0).getName());
		List<Long> ids = new ArrayList<>();
		ids.add(newPet.getId());
		newCustomer.setPetIds(ids);
		userController.saveCustomer(newCustomer);
		//check to make sure customer now also contains pet
		List<CustomerDTO> customerDTOList = userController.getAllCustomers();
		Assertions.assertTrue(!customerDTOList.isEmpty());
		CustomerDTO retrievedCustomer = userController.getAllCustomers().get(0);
		Assertions.assertTrue(retrievedCustomer.getId() > 0L);
		Assertions.assertTrue(!retrievedCustomer.getPetIds().isEmpty());
		Assertions.assertTrue(retrievedCustomer.getPetIds() != null && retrievedCustomer.getPetIds().size() > 0);
		Assertions.assertEquals(retrievedCustomer.getPetIds().get(0), retrievedPet.getId());
	}

	@Test
	public void testFindPetsByOwner() {
		CustomerDTO customerDTO = createCustomerDTO();
		CustomerDTO newCustomer = userController.saveCustomer(customerDTO);

		PetDTO petDTO = createPetDTO();
		petDTO.setOwnerId(newCustomer.getId());
		PetDTO newPet = petController.savePet(petDTO);
		petDTO.setType(PetType.DOG);
		petDTO.setName("DogName");
		PetDTO newPet2 = petController.savePet(petDTO);
		Assertions.assertTrue(newPet2.getId() > 0L);
		Assertions.assertTrue(newPet2.getOwnerId() > 0L);
		List<PetDTO> pets = petController.getPetsByOwner(newCustomer.getId());
		Assertions.assertTrue(pets.size() > 0);
		Assertions.assertEquals(pets.get(0).getOwnerId(), newCustomer.getId());
		Assertions.assertEquals(pets.get(0).getId(), newPet.getId());
	}

	@Test
	public void testFindOwnerByPet() {
		CustomerDTO customerDTO = createCustomerDTO();
		CustomerDTO newCustomer = userController.saveCustomer(customerDTO);

		PetDTO petDTO = createPetDTO();
		petDTO.setOwnerId(newCustomer.getId());
		PetDTO newPet = petController.savePet(petDTO);
		List<Long> ids = new ArrayList<>();
		if (!ids.contains(newPet.getId())){
			ids.add(newPet.getId());
		}
		newCustomer.setPetIds(ids);
		userController.saveCustomer(newCustomer);

		Assertions.assertTrue(newPet.getId() > 0L);
		Assertions.assertTrue(newPet.getOwnerId() > 0L);
		CustomerDTO owner = userController.getOwnerByPet(newPet.getId());

		Assertions.assertEquals(owner.getId(), newCustomer.getId());
		Assertions.assertEquals(owner.getPetIds().get(0), newPet.getId());
	}

	@Test
	public void testChangeEmployeeAvailability() {
		EmployeeDTO employeeDTO = createEmployeeDTO();
		EmployeeDTO emp1 = userController.saveEmployee(employeeDTO);
		Assertions.assertNull(emp1.getDaysAvailable());

		Set<DayOfWeek> availability = Sets.newHashSet(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY);
		userController.setAvailability(availability, emp1.getId());

		EmployeeDTO emp2 = userController.getEmployee(emp1.getId());
		Assertions.assertNotNull(emp2);
		Assertions.assertEquals(availability, emp2.getDaysAvailable());
	}

	@Test
	public void testFindEmployeesByServiceAndTime() {
		EmployeeDTO emp1 = createEmployeeDTO();
		EmployeeDTO emp2 = createEmployeeDTO();
		EmployeeDTO emp3 = createEmployeeDTO();

		emp1.setDaysAvailable(Sets.newHashSet(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
		emp2.setDaysAvailable(Sets.newHashSet(DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
		emp3.setDaysAvailable(Sets.newHashSet(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));

		emp1.setSkills(Sets.newHashSet(EmployeeSkill.FEEDING, EmployeeSkill.PETTING));
		emp2.setSkills(Sets.newHashSet(EmployeeSkill.PETTING, EmployeeSkill.WALKING));
		emp3.setSkills(Sets.newHashSet(EmployeeSkill.WALKING, EmployeeSkill.SHAVING));

		EmployeeDTO emp1n = userController.saveEmployee(emp1);
		EmployeeDTO emp2n = userController.saveEmployee(emp2);
		EmployeeDTO emp3n = userController.saveEmployee(emp3);

		//make a request that matches employee 1 or 2
		EmployeeRequestDTO er1 = new EmployeeRequestDTO();
		er1.setDate(LocalDate.of(2019, 12, 25)); //wednesday
		er1.setSkills(Sets.newHashSet(EmployeeSkill.PETTING));
        List<EmployeeDTO> employeeDTOList = userController.findEmployeesForService((er1));
		Assertions.assertTrue(!employeeDTOList.isEmpty());
		Set<Long> eIds1 = employeeDTOList.stream().map(EmployeeDTO::getId).collect(Collectors.toSet());
		System.out.println("eIds1 " + eIds1);
		Set<Long> eIds1expected = Sets.newHashSet(emp1n.getId(), emp2n.getId());

		Assertions.assertEquals(eIds1, eIds1expected);

		//make a request that matches only employee 3
		EmployeeRequestDTO er2 = new EmployeeRequestDTO();
		er2.setDate(LocalDate.of(2019, 12, 27)); //friday
		er2.setSkills(Sets.newHashSet(EmployeeSkill.WALKING, EmployeeSkill.SHAVING));

		Set<Long> eIds2 = userController.findEmployeesForService(er2).stream().map(EmployeeDTO::getId).collect(Collectors.toSet());
		Set<Long> eIds2expected = Sets.newHashSet(emp3n.getId());
		Assertions.assertEquals(eIds2, eIds2expected);
	}

	@Test
	public void testSchedulePetsForServiceWithEmployee() {
		EmployeeDTO employeeTemp = createEmployeeDTO();
		employeeTemp.setDaysAvailable(Sets.newHashSet(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
		EmployeeDTO employeeDTO = userController.saveEmployee(employeeTemp);
		CustomerDTO customerDTO = userController.saveCustomer(createCustomerDTO());
		PetDTO petTemp = createPetDTO();
		petTemp.setOwnerId(customerDTO.getId());
		PetDTO petDTO = petController.savePet(petTemp);

		LocalDate date = LocalDate.of(2019, 12, 25);
		List<Long> petList = Lists.newArrayList(petDTO.getId());
		List<Long> employeeList = Lists.newArrayList(employeeDTO.getId());
		Set<EmployeeSkill> skillSet =  Sets.newHashSet(EmployeeSkill.PETTING);

		scheduleController.createSchedule(createScheduleDTO(petList, employeeList, date, skillSet));
		ScheduleDTO scheduleDTO = scheduleController.getAllSchedules().get(0);
		Assertions.assertNotNull(scheduleDTO.getId());
		Assertions.assertTrue(scheduleDTO.getId() > 0L);
		Assertions.assertNotNull(scheduleDTO.getActivities());
		Assertions.assertEquals(scheduleDTO.getActivities(), skillSet);
		Assertions.assertEquals(scheduleDTO.getDate(), date);
		Assertions.assertTrue(scheduleDTO.getEmployeeIds().containsAll(employeeList));
		Assertions.assertTrue(scheduleDTO.getPetIds().containsAll(petList));
	}

	@Test
	public void testFindScheduleByEntities() {
		ScheduleDTO sched1 = populateSchedule(1, 2, LocalDate.of(2019, 12, 25), Sets.newHashSet(EmployeeSkill.FEEDING, EmployeeSkill.WALKING));
		Assertions.assertNotNull(sched1.getPetIds());
		Assertions.assertTrue(!sched1.getPetIds().isEmpty());
		Assertions.assertNotNull(sched1.getEmployeeIds());
		Assertions.assertTrue(!sched1.getEmployeeIds().isEmpty());

		ScheduleDTO sched2 = populateSchedule(3, 1, LocalDate.of(2019, 12, 26), Sets.newHashSet(EmployeeSkill.PETTING));

		//add a third schedule that shares some employees and pets with the other schedules
		ScheduleDTO sched3 = new ScheduleDTO();
		sched3.setEmployeeIds(sched1.getEmployeeIds());
		sched3.setPetIds(sched2.getPetIds());
		sched3.setActivities(Sets.newHashSet(EmployeeSkill.SHAVING, EmployeeSkill.PETTING));
		sched3.setDate(LocalDate.of(2020, 3, 23));
		scheduleController.createSchedule(sched3);

        /*
            We now have 3 schedule entries. The third schedule entry has the same employees as the 1st schedule
            and the same pets/owners as the second schedule. So if we look up schedule entries for the employee from
            schedule 1, we should get both the first and third schedule as our result.
         */

		//Employee 1 in is both schedule 1 and 3
		List<ScheduleDTO> scheds1e = scheduleController.getScheduleForEmployee(sched1.getEmployeeIds().get(0));
		compareSchedules(sched1, scheds1e.get(0));
		compareSchedules(sched3, scheds1e.get(1));

		//Employee 2 is only in schedule 2
		List<ScheduleDTO> scheds2e = scheduleController.getScheduleForEmployee(sched2.getEmployeeIds().get(0));
		compareSchedules(sched2, scheds2e.get(0));

		//Pet 1 is only in schedule 1
		List<ScheduleDTO> scheds1p = scheduleController.getScheduleForPet(sched1.getPetIds().get(0));
		compareSchedules(sched1, scheds1p.get(0));

		//Pet from schedule 2 is in both schedules 2 and 3
		List<ScheduleDTO> scheds2p = scheduleController.getScheduleForPet(sched2.getPetIds().get(0));
		compareSchedules(sched2, scheds2p.get(0));
		compareSchedules(sched3, scheds2p.get(1));

		//Owner of the first pet will only be in schedule 1
		Long ownerId = userController.getOwnerByPet(sched1.getPetIds().get(0)).getId();
		List<ScheduleDTO> scheds1c = scheduleController.getScheduleForCustomer(ownerId);
		compareSchedules(sched1, scheds1c.get(0));

		//Owner of pet from schedule 2 will be in both schedules 2 and 3
		List<ScheduleDTO> scheds2c = scheduleController.getScheduleForCustomer(userController.getOwnerByPet(sched2.getPetIds().get(0)).getId());
		compareSchedules(sched2, scheds2c.get(0));
		compareSchedules(sched3, scheds2c.get(1));
	}


	private static EmployeeDTO createEmployeeDTO() {
		EmployeeDTO employeeDTO = new EmployeeDTO();
		employeeDTO.setName("TestEmployee");
		employeeDTO.setSkills(Sets.newHashSet(EmployeeSkill.FEEDING, EmployeeSkill.PETTING));
		return employeeDTO;
	}
	private static CustomerDTO createCustomerDTO() {
		CustomerDTO customerDTO = new CustomerDTO();
		customerDTO.setName("TestEmployee");
		customerDTO.setPhoneNumber("123-456-789");
		customerDTO.setNotes("test");
		return customerDTO;
	}

	private static PetDTO createPetDTO() {
		PetDTO petDTO = new PetDTO();
		petDTO.setName("TestPet");
		petDTO.setType(PetType.CAT);
		return petDTO;
	}

	private static EmployeeRequestDTO createEmployeeRequestDTO() {
		EmployeeRequestDTO employeeRequestDTO = new EmployeeRequestDTO();
		employeeRequestDTO.setDate(LocalDate.of(2019, 12, 25));
		employeeRequestDTO.setSkills(Sets.newHashSet(EmployeeSkill.FEEDING, EmployeeSkill.WALKING));
		return employeeRequestDTO;
	}

	private static ScheduleDTO createScheduleDTO(List<Long> petIds, List<Long> employeeIds, LocalDate date, Set<EmployeeSkill> activities) {
		ScheduleDTO scheduleDTO = new ScheduleDTO();
		scheduleDTO.setPetIds(petIds);
		scheduleDTO.setEmployeeIds(employeeIds);
		scheduleDTO.setDate(date);
		scheduleDTO.setActivities(activities);
		return scheduleDTO;
	}

	private ScheduleDTO populateSchedule(int numEmployees, int numPets, LocalDate date, Set<EmployeeSkill> activities) {
		List<Long> employeeIds = IntStream.range(0, numEmployees)
				.mapToObj(i -> createEmployeeDTO())
				.map(e -> {
					e.setSkills(activities);
					e.setDaysAvailable(Sets.newHashSet(date.getDayOfWeek()));
					return userController.saveEmployee(e).getId();
				}).collect(Collectors.toList());
		CustomerDTO cust = userController.saveCustomer(createCustomerDTO());
		List<Long> petIds = IntStream.range(0, numPets)
				.mapToObj(i -> createPetDTO())
				.map(p -> {
					p.setOwnerId(cust.getId());
					return petController.savePet(p).getId();
				}).collect(Collectors.toList());
		cust.setPetIds(petIds);
		userController.saveCustomer(cust);
		return scheduleController.createSchedule(createScheduleDTO(petIds, employeeIds, date, activities));
	}

	private static void compareSchedules(ScheduleDTO sched1, ScheduleDTO sched2) {
		Assertions.assertEquals(sched1.getPetIds(), sched2.getPetIds());
		Assertions.assertEquals(sched1.getActivities(), sched2.getActivities());
		Assertions.assertEquals(sched1.getEmployeeIds(), sched2.getEmployeeIds());
		Assertions.assertEquals(sched1.getDate(), sched2.getDate());
	}
}
