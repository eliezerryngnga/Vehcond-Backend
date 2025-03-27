package vehcon.services.appdata;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;

import lombok.Data;

import java.util.List;

import vehcon.repo.appdata.PageUrlRepository;
import vehcon.repo.appdata.UserPagesRepository;



import vehcon.dto.appdata.MenuLink;
import vehcon.models.appdata.*;


@Service
public class MenuService {

    @Autowired
    private UserPagesRepository userPagesRepository;

    @Autowired
    private PageUrlRepository pageUrlRepository;

    public List<PageUrl> getMenuByRole(Integer rolecode) {
        
        List<UserPages> userPages = userPagesRepository.findByRolecode(rolecode);
        return userPages.stream().map(UserPages::getPageUrl).collect(Collectors.toList());
    }
        
}
