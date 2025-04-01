package vehcon.services.masters;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vehcon.models.masters.FinancialYear;
import vehcon.repo.masters.FinancialYearRepo;

@Service
@RequiredArgsConstructor
public class FinancialYearService {

	private final FinancialYearRepo financialYearRepo;

	public List<FinancialYear> getFinancialYear()
	{
		return financialYearRepo.findAll();
	}
}