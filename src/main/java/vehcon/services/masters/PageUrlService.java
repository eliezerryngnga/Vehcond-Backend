package vehcon.services.masters;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vehcon.models.masters.PageUrl;
import vehcon.repo.masters.PageUrlRepository;

@Service
@RequiredArgsConstructor
public class PageUrlService {

	private final PageUrlRepository pageUrlRepo;
	
	@Transactional
	public List<PageUrl> getPageUrls()
	{
		return pageUrlRepo.findAll();
	}
	
}
