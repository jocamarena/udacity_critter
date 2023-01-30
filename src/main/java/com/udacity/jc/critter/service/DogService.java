package com.udacity.jc.critter.service;

import com.udacity.jc.critter.dataaccess.DogRepository;
import com.udacity.jc.critter.domain.Dog;
import com.udacity.jc.critter.pet.PetDTO;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DogService {
    private DogRepository dogRepository;
    public DogService(DogRepository dogRepository){
        this.dogRepository = dogRepository;
    }
    public Dog saveDog(Dog dog){
        return dogRepository.save(dog);
    }
    public List<PetDTO> findAllDogs(){
        List allDogsDTO = new ArrayList<>();
        List allDogs = dogRepository.findAll();
        Iterator<Dog> itr = allDogs.iterator();
        while (itr.hasNext()){
            PetDTO petDTO = new PetDTO();
            BeanUtils.copyProperties(itr.next(), petDTO);
            allDogsDTO.add(petDTO);
        }
        return allDogsDTO;
    }
    public List<PetDTO> findAllByOwnerId(Long id){
        List<Dog> dogs = dogRepository.findAllByOwnerId(id);
        List<PetDTO> petDTOS = new ArrayList<>();
        Iterator itr = dogs.iterator();
        while (itr.hasNext()){
            PetDTO petDTO = new PetDTO();
            BeanUtils.copyProperties(itr.next(), petDTO);
            petDTOS.add(petDTO);
        }
        System.out.println("fingAllByOwner: " + dogs.size() +
                " allPetDTO's: " + petDTOS.size());
        return petDTOS;
    }
    public Optional<Dog> findDogById(Long id){
        return dogRepository.findById(id);
    }
}
