package com.udacity.jc.critter.pet;

import com.udacity.jc.critter.domain.Customer;
import com.udacity.jc.critter.domain.Dog;
import com.udacity.jc.critter.service.CustomerService;
import com.udacity.jc.critter.service.DogService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pet")
public class PetController {
    private DogService dogService;
    private CustomerService customerService;
    public PetController(DogService dogService, CustomerService customerService){
        this.customerService = customerService;
        this.dogService = dogService;
    }

    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) {
        Dog dog = new Dog();
        BeanUtils.copyProperties(petDTO, dog);
        dog = dogService.saveDog(dog);
        BeanUtils.copyProperties(dog, petDTO);
        Optional<Customer> optionalCustomer = customerService.findCustomerById(dog.getOwnerId());
        if (optionalCustomer.isPresent()){
            Customer customer = optionalCustomer.get();
            List<Long> petIds = new ArrayList<>();
            if (customer.getPetIds() != null && !customer.getPetIds().isEmpty()) {
                petIds.addAll(customer.getPetIds());
            }
            petIds.add(dog.getId());
            customer.setPetIds(petIds);
            customerService.saveCustomer(customer);
        }
        return petDTO;
    }

    @GetMapping("/{petId}")
    public PetDTO getPet(@PathVariable long petId) {
        PetDTO petDTO = new PetDTO();
        Optional<Dog> dogOptional = dogService.findDogById(petId);
        if (dogOptional.get() != null){
            BeanUtils.copyProperties(dogOptional.get(), petDTO);
        }
        return petDTO;
    }

    @GetMapping
    public List<PetDTO> getPets(){
        List allDogs = dogService.findAllDogs();
        List allDogsDTO = new ArrayList<>();
        Iterator<Dog> itr = allDogs.iterator();
        while (itr.hasNext()){
            PetDTO petDTO = new PetDTO();
            BeanUtils.copyProperties(itr.next(), petDTO);
            allDogsDTO.add(petDTO);
        }
        return allDogsDTO;
    }

    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {
        return dogService.findAllByOwnerId(ownerId);
    }
}
