package vehcon.controller.appdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.io.IOException;
import vehcon.dto.appdata.MenuLink;
import vehcon.exception.InternalServerError;
import vehcon.exception.UnauthorizedException;
import vehcon.models.appdata.PageUrl;
import vehcon.models.appdata.UserPages;
import vehcon.models.auth.Roles;
import vehcon.models.auth.User;
import vehcon.repo.auth.RolesRepository;
import vehcon.services.appdata.MenuService;
import vehcon.services.auth.AuthenticationService;

import java.util.List;

@RestController  

public class MenuController {

    @Autowired
    private MenuService menuService;
    @Autowired private RolesRepository rolesRepo;

    
    @GetMapping("/menu")
    public List<PageUrl> refreshToken(@AuthenticationPrincipal User user) throws IOException {
    	
    	try
    	{
    		Roles role = rolesRepo.findByRole(user.getRole().name()).orElseThrow();
    		return menuService.getMenuByRole(role.getRolecode());
    	}
    	catch (UnauthorizedException ex)
    	{
    		throw ex;
    	}
    	catch (Exception ex)
    	{
    		throw new InternalServerError("Unacle to fetch menu.", ex);
    	}
    }
}
