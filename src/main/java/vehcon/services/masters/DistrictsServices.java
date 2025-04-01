package vehcon.services.masters;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vehcon.models.masters.Districts;
import vehcon.repo.masters.DistrictsRepository;

@Service
public class DistrictsServices {
	
	@Autowired
	DistrictsRepository districtNameRepository;
	
	public List<Districts> getByDistricts()
	{
		return districtNameRepository.findAll();
	}
}
