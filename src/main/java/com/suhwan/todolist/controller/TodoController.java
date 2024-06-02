package com.suhwan.todolist.controller;

import com.suhwan.todolist.domain.Todo;
import com.suhwan.todolist.dto.AddTodoRequest;
import com.suhwan.todolist.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/todo")
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public ResponseEntity<Iterable<Todo>> getTodos(Principal principal) {

        return ResponseEntity.ok(todoService.getTodos(principal.getName()));
//        return ResponseEntity.ok(todoService.getTodos());
    }


    @PostMapping
    public ResponseEntity<Todo> insertTodo(@RequestBody AddTodoRequest request, Principal principal) {
        Todo savedTodo = todoService.insertTodo(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTodo);
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<Todo> updateTodo(@PathVariable("todoId") Long todoId) throws Exception {
        return ResponseEntity.ok(todoService.updateTodo(todoId));
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> deleteTodo(@PathVariable("todoId") Long todoId) {
        todoService.deleteTodo(todoId);
        return ResponseEntity.noContent().build();
    }
}
