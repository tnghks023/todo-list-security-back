package com.suhwan.todolist.service;

import com.suhwan.todolist.domain.Todo;
import com.suhwan.todolist.domain.User;
import com.suhwan.todolist.dto.AddTodoRequest;
import com.suhwan.todolist.repository.TodoRepository;
import com.suhwan.todolist.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public Page<Todo> getTodos(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return todoRepository.findAllByAuthor(email, pageable);
    }

    public Todo insertTodo(AddTodoRequest request, String userName) {
        User user = userRepository.findByEmail(userName).orElseThrow(IllegalAccessError::new);
        return todoRepository.save(request.toEntity(userName, user));
    }

    @Transactional
    public Todo updateTodo(Long todoId) throws Exception {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new Exception("Todo not found!"));
        todo.setCompleted(!todo.isCompleted());
        return todoRepository.save(todo);
    }

    public void deleteTodo(Long todoId) {
        todoRepository.deleteById(todoId);
    }
}
