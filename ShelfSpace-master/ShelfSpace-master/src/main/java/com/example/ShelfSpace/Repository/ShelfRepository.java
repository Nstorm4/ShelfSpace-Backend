package com.example.ShelfSpace.Repository;

import com.example.ShelfSpace.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, String> {
    List<Shelf> findByUser(User user);
}
