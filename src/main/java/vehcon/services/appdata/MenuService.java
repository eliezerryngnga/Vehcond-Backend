package vehcon.services.appdata;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vehcon.models.masters.PageUrl;
import vehcon.models.masters.UserPages;
import vehcon.repo.appdata.UserPagesRepository;


@Service
@RequiredArgsConstructor 
public class MenuService {

    private final UserPagesRepository userPagesRepository;

//    private final PageUrlRepository pageUrlRepository;

    public List<PageUrl> getMenuByRole(Integer rolecode) {
        
        List<UserPages> userPages = userPagesRepository.findByIdRoleCode(rolecode);
        return userPages.stream().map(UserPages::getPageUrl).collect(Collectors.toList());
    }
    
}
