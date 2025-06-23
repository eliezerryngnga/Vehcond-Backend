package vehcon.services.masters;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vehcon.annotations.Auditable;
import vehcon.common.data.util.SpecificationUtils;
import vehcon.dto.masters.ProcessesDTO;
import vehcon.models.masters.Processes;
import vehcon.repo.appdata.ProcessesRepository;

@Service
@RequiredArgsConstructor
public class ProcessesService {

	private final ProcessesRepository processesRepository;
	
	@Transactional
	public Optional<Processes> getByProccessCode(Integer processCode)
	{
		return processesRepository.findById(processCode);
	}
	
	@Auditable
	@Transactional
	public Processes addProcess(ProcessesDTO dto) {
        Processes process = new Processes();
        process.setProcessname(dto.getProcessName());
        process.setProcessdescription(dto.getProcessDescription());
        return processesRepository.save(process);
    }
	
	@Transactional(readOnly = true)
	public Page<Processes> getAllProcesses(
			String globalSearchTerm,
			Pageable pageable
			)
	{
		Specification<Processes> spec = Specification.where(null);

        if (globalSearchTerm != null && !globalSearchTerm.trim().isEmpty()) {
            List<String> globalSearchFields = List.of("processname","processdescription");
            Specification<Processes> globalSearchSpec = SpecificationUtils.searchInFields(globalSearchTerm, globalSearchFields);
            if (globalSearchSpec != null) 
            {
                spec = spec.and(globalSearchSpec);
            }
        }
        return processesRepository.findAll(spec, pageable);
    }
}
