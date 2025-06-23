package vehcon.controller.masters;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vehcon.dto.masters.ProcessesDTO;
import vehcon.models.auth.User;
import vehcon.models.masters.Processes;
import vehcon.services.masters.ProcessesService;

@RestController
@RequestMapping("/processes")
@RequiredArgsConstructor
public class ProcessesController {
	
	private final ProcessesService processService;
	
	@GetMapping("/list")
	public ResponseEntity<Page<Processes>> getAllProcesses(
			@RequestParam(name = "search", required = false) String searchTerm,
			@PageableDefault(sort = "processcode", direction = Sort.Direction.ASC)Pageable pageable,
			@AuthenticationPrincipal User user)
	{
		try
		{
			Page<Processes> processPage = processService.getAllProcesses(searchTerm, pageable);
			return ResponseEntity.ok(processPage);
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}
	}
	
	@PostMapping("/add-processes")
	public ResponseEntity<String> addProcesses(@RequestBody ProcessesDTO dto)
	{
		try
		{
			Processes savedProcesses = processService.addProcess(dto);
			
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Process " + savedProcesses.getProcessname() + "created successfully");
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to create process: " + e.getMessage());
		}
	}
}
