package com.mrsarayra.agileengine.controllers;

import com.mrsarayra.agileengine.dao.PhotoEntity;
import com.mrsarayra.agileengine.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/")
public class SearchController {

    @Autowired
    private PhotoService photoService;


    @GetMapping("/search/{searchTerm}")
    public ResponseEntity<List<PhotoEntity>> search(@PathVariable(name = "searchTerm") String searchTerm) {
        return ResponseEntity.ok(photoService.findPhotoByTerm(searchTerm));
    }


    @GetMapping("/images/{id}")
    public ResponseEntity<PhotoEntity> getById(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(photoService.findById(id));
    }

}
