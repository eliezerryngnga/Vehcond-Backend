package vehcon.services.masters;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vehcon.models.masters.DistrictRto;
import vehcon.repo.masters.DistrictRtoRepository;

@Service
@RequiredArgsConstructor
public class DistrictRtoService {
	
	private final DistrictRtoRepository districtRtoRepository;
	
	public List<DistrictRto> getDistrictRto()
	{			
		return districtRtoRepository.findAll();
	}
	
}
