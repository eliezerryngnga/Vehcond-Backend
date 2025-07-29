package vehcon.services.masters;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vehcon.dto.masters.PageUrlDTO;
import vehcon.models.masters.PageUrl;
import vehcon.repo.masters.PageUrlRepository;

@Service
@RequiredArgsConstructor
public class PageUrlService {

	private final PageUrlRepository pageUrlRepo;
	
	@Transactional
	public List<PageUrlDTO> getPageUrls()
	{
		List<PageUrl> pageEntities = pageUrlRepo.findAll();
		return pageEntities.stream().map(this::toPageUrlDTO)
				.collect(Collectors.toList());
	}
	
	private PageUrlDTO toPageUrlDTO(PageUrl entity)
	{
		PageUrlDTO dto = new PageUrlDTO();
		
		dto.setUrlCode(entity.getUrlCode());
		dto.setProcessName(entity.getProcessName());
		dto.setSubProcessName(entity.getSubProcessName());
		dto.setPageUrl(entity.getPageurl());
		
		return dto;
	}
}
