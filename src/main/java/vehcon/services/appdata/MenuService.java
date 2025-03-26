package vehcon.services.appdata;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;

import lombok.Data;

import java.util.List;

import vehcon.repo.auth.PageUrlRepository;
import vehcon.repo.auth.UserPageRepository;
import vehcon.dto.appdata.MenuLink;
import vehcon.models.fetch.*;


@Service
public class MenuService {

    @Autowired
    private UserPageRepository userPagesRepository;

    @Autowired
    private PageUrlRepository pageUrlRepository;

    public List<MenuLink> getMenuLinks(Integer rolecode) {
        
        List<UserPages> userPages = userPagesRepository.findByRole(rolecode);
        System.out.println("User Pages: "+userPages);
        return null;
        //return userPages.stream()
          //      .filter(up -> up.getPageUrl().)
            //    .map(up -> new MenuLink(up.getPageUrl().getPageurl(), up.getPageUrl().getSubProcessName(), up.getPageUrl().getSubProcessIcon()))
              //  .collect(Collectors.toList());
    }
    
    public List<UserPages> getMenu(Integer rolecode) {
        
        List<UserPages> userPages = userPagesRepository.findAll();
        return userPages;
        //return userPages.stream()
          //      .filter(up -> up.getPageUrl().)
            //    .map(up -> new MenuLink(up.getPageUrl().getPageurl(), up.getPageUrl().getSubProcessName(), up.getPageUrl().getSubProcessIcon()))
              //  .collect(Collectors.toList());
    }
}
