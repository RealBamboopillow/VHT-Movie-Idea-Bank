package com.example.application.services;

import com.example.application.data.MovieIdea;
import com.example.application.data.MovieIdeaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class MovieIdeaService {

    private final MovieIdeaRepository repository;

    public MovieIdeaService(MovieIdeaRepository repository) {
        this.repository = repository;
    }

    public Optional<MovieIdea> get(Long id) {
        return repository.findById(id);
    }

    public MovieIdea save(MovieIdea entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<MovieIdea> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<MovieIdea> list(Pageable pageable, Specification<MovieIdea> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
    
    public List<MovieIdea> findAll(Specification<MovieIdea> spec) {
        return repository.findAll(spec);
    }
    
}
