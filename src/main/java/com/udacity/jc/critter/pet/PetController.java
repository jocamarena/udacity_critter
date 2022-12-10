package com.udacity.jc.critter.pet;

import com.udacity.jc.critter.domain.Dog;
import com.udacity.jc.critter.service.DogService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pet")
public class PetController {
    private DogService dogService;
    public PetController(DogService dogService){
        this.dogService = dogService;
    }

    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) {
        Dog dog = new Dog();
        BeanUtils.copyProperties(petDTO, dog);
        dog = dogService.saveDog(dog);
        BeanUtils.copyProperties(dog, petDTO);
        System.out.println("petDTO id and owner id: " + petDTO.getId() + " " + petDTO.getOwnerId());
        System.out.println("newPet id and owner id:" + dog.getId() + " " + dog.getOwnerId());
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
        throw new UnsupportedOperationException();
    }

    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {
        return dogService.findAllByOwnerId(ownerId);
    }
}
