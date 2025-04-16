package org.example.expert.domain.todo.repository;

import java.util.Optional;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TodoRepository extends JpaRepository<Todo, Long> {

	@EntityGraph(attributePaths = "user")
	Page<Todo> findAllWithUserByOrderByModifiedAtDesc(Pageable pageable);

	@Query("SELECT t FROM Todo t " +
		"LEFT JOIN FETCH t.user " +
		"WHERE t.id = :todoId")
	Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

}
