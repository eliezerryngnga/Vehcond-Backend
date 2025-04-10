package vehcon.services.masters;

import java.util.Optional;

import org.springframework.stereotype.Service;

import vehcon.models.masters.Processes;
import vehcon.repo.appdata.ProcessesRepository;

@Service
public class ProcessesService {

	private ProcessesRepository processesRepository;
	
	public Optional<Processes> getByProccessCode(Integer processCode)
	{
		return processesRepository.findById(processCode);
	}
	
}
