package vehcon.controller.masters;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vehcon.models.masters.PageUrl;
import vehcon.services.masters.PageUrlService;

@RestController
@RequestMapping("/pageUrl")
@RequiredArgsConstructor
public class PageUrlController {
	
	private final PageUrlService pageUrlService;
	
	@GetMapping
	public ResponseEntity<List<PageUrl>> getAllPageUrls() {
		try
		{
			List<PageUrl> pageUrls = pageUrlService.getPageUrls();
			return ResponseEntity.ok(pageUrls);
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}
	}
}
