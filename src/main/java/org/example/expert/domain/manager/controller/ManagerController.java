package org.example.expert.domain.manager.controller;

import java.util.List;

import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.service.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/todos/{todoId}/managers")
@RequiredArgsConstructor
public class ManagerController {

	private final ManagerService managerService;

	@PostMapping
	public ResponseEntity<ManagerSaveResponse> saveManager(
		@Auth AuthUser authUser,
		@PathVariable long todoId,
		@Valid @RequestBody ManagerSaveRequest managerSaveRequest
	) {
		return ResponseEntity.ok(managerService.saveManager(authUser, todoId, managerSaveRequest));
	}

	@GetMapping
	public ResponseEntity<List<ManagerResponse>> getMembers(@PathVariable long todoId) {
		return ResponseEntity.ok(managerService.getManagers(todoId));
	}

	@DeleteMapping("/{managerId}")
	public void deleteManager(
		@Auth AuthUser authUser,
		@PathVariable long todoId,
		@PathVariable long managerId
	) {
		managerService.deleteManager(authUser, todoId, managerId);
	}
}
