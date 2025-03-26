package vehcon.controller.appdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vehcon.dto.appdata.MenuLink;
import vehcon.models.auth.Roles;
import vehcon.models.auth.User;
import vehcon.models.fetch.PageUrl;
import vehcon.models.fetch.UserPages;
import vehcon.repo.auth.RolesRepository;
import vehcon.services.appdata.MenuService;
import vehcon.services.auth.AuthenticationService;

import java.util.List;

@RestController  

public class MenuController {

    @Autowired
    private MenuService menuService;
    @Autowired RolesRepository rolesRepo;

    
    @GetMapping("/menu")
    public List<UserPages> getMenuLinks(@AuthenticationPrincipal User user) {
    	Roles role1 = rolesRepo.findByRole(user.getRole().name()).orElseThrow();
        //return menuService.getMenuLinks(role1.getRolecode());
    	return menuService.getMenu(role1.getRolecode());
    }
}
