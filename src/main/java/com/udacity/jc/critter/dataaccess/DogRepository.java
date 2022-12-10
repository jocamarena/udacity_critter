package com.udacity.jc.critter.dataaccess;

import com.udacity.jc.critter.domain.Dog;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DogRepository extends JpaRepository<Dog, Long> {
    public List<Dog> findAllByOwnerId(Long id);
}
