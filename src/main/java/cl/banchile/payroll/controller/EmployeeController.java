package cl.banchile.payroll.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cl.banchile.payroll.exception.EmployeeNotFoundException;
import cl.banchile.payroll.model.Employee;
import cl.banchile.payroll.model.EmployeeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
class EmployeeController {

	private final EmployeeRepository repository;

	EmployeeController(EmployeeRepository repository) {
		this.repository = repository;
	}

	// Aggregate root
	// tag::get-aggregate-root[]
	@GetMapping("/employees")
	List<Employee> all() {
		return repository.findAll();
	}
	// end::get-aggregate-root[]

	@PostMapping("/employees")
	Employee newEmployee(@RequestBody Employee newEmployee) {
		return repository.save(newEmployee);
	}

	// Single item
	@Operation(summary = "Obtener un empleado por id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Empleado encontrado", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class)) }),
			@ApiResponse(responseCode = "400", description = "Se proporciono un id válida", content = @Content),
			@ApiResponse(responseCode = "404", description = "Empleado no entrado", content = @Content) })
	@GetMapping("/employees/{id}")
	Employee one(@PathVariable Long id) {

		return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
	}

	@PutMapping("/employees/{id}")
	Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {

		return repository.findById(id).map(employee -> {
			employee.setName(newEmployee.getName());
			employee.setRole(newEmployee.getRole());
			return repository.save(employee);
		}).orElseGet(() -> {
			newEmployee.setId(id);
			return repository.save(newEmployee);
		});
	}

	@DeleteMapping("/employees/{id}")
	void deleteEmployee(@PathVariable Long id) {
		repository.deleteById(id);
	}
}
