package com.suhwan.todolist.repository;

import com.suhwan.todolist.domain.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    Page<Todo> findAllByAuthor(String email, Pageable pageable);
}
