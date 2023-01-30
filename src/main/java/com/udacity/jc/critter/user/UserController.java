package com.udacity.jc.critter.user;

import com.udacity.jc.critter.domain.Customer;
import com.udacity.jc.critter.domain.Employee;
import com.udacity.jc.critter.domain.Person;
import com.udacity.jc.critter.service.CustomerService;
import com.udacity.jc.critter.service.DogService;
import com.udacity.jc.critter.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private CustomerService customerService;
    private EmployeeService employeeService;
    private DogService dogService;
    public UserController(CustomerService customerService, EmployeeService employeeService, DogService dogService){
        this.customerService = customerService;
        this.employeeService = employeeService;
        this.dogService = dogService;
    }
    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        Customer newCustomer;
        Optional<Customer> optionalCustomer = customerService.findCustomerById(customerDTO.getId());
        if (!optionalCustomer.isEmpty() && optionalCustomer.get() != null){
            newCustomer = optionalCustomer.get();
        } else newCustomer = new Customer();
        BeanUtils.copyProperties(customerDTO, newCustomer);
        customerService.saveCustomer(newCustomer);
        BeanUtils.copyProperties(newCustomer,customerDTO);
        return customerDTO;
    }

    public Optional<Customer> findCustomerById(Long id){
        return customerService.findCustomerById(id);
    }
    public Iterable<Customer> findAllCustomers(){
        return customerService.findAllCustomers();
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers(){
        List<CustomerDTO> customerDTOList = new ArrayList<>();
        Iterable<Customer> customerIterable = customerService.findAllCustomers();
        Iterator itr = customerIterable.iterator();
        while (itr.hasNext()){
            CustomerDTO customerDTO = new CustomerDTO();
            BeanUtils.copyProperties(itr.next(), customerDTO);
            customerDTOList.add(customerDTO);
        }
        return customerDTOList;
    }

    @GetMapping("/customer/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId){
        List<Customer> customers = new ArrayList<>();
        CustomerDTO customerDTO = new CustomerDTO();
        Customer customer = customerService.findByPetId(petId);
        if (customer != null) {
            BeanUtils.copyProperties(customer, customerDTO);
            return customerDTO;
        } else {
            customerService.findAllCustomers().forEach(cust -> customers.add(cust));
        }
        Optional<Customer> optionalCustomer = customers.stream().findFirst();
        BeanUtils.copyProperties(optionalCustomer.get(), customerDTO);
        return customerDTO;
    }

    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employee = employeeService.saveEmployee(employee);
        BeanUtils.copyProperties(employee, employeeDTO);
        return employeeDTO;
    }

    @PostMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable Long employeeId) {
        Optional<Employee> optionalEmployee = employeeService.findEmployeeById(employeeId);
        EmployeeDTO employeeDTO = new EmployeeDTO();
        Employee employee = optionalEmployee.get();
        BeanUtils.copyProperties(employee, employeeDTO);
        return employeeDTO;
    }

    @PutMapping("/employee/{employeeId}")
    public EmployeeDTO setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId) {
        Optional<Employee> optionalEmployee = employeeService.findEmployeeById(employeeId);
            Employee employee = optionalEmployee.get();
            EmployeeDTO employeeDTO = new EmployeeDTO();
            employee.setDaysAvailable(daysAvailable);
            employeeService.saveEmployee(employee);
            BeanUtils.copyProperties(employee, employeeDTO);
            return employeeDTO;
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeDTO) {
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        Iterable<Employee> employeeIterable = employeeService.findAllEmployees();
        Iterator<Employee> itr = employeeIterable.iterator();
        while (itr.hasNext()){
            Employee employee = itr.next();
            Set<EmployeeSkill> skills = employee.getSkills();
            Set<DayOfWeek> dayOfWeekSet = employee.getDaysAvailable();
            DayOfWeek dateRequested = employeeDTO.getDate().getDayOfWeek();
            Set<EmployeeSkill> skillsRequested = employeeDTO.getSkills();
            boolean dateMatch = dayOfWeekSet.stream().anyMatch(day -> day == dateRequested);
            boolean skillMatch = skillsRequested.stream().allMatch(skill -> skills.contains(skill));
            if (skillMatch && dateMatch){
                EmployeeDTO newEmployeeDTO = new EmployeeDTO();
                BeanUtils.copyProperties(employee, newEmployeeDTO);
                employeeDTOList.add(newEmployeeDTO);
            }
        }
        return employeeDTOList;
    }
}
