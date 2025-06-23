//package vehcon.services.appdata;
//
//import org.springframework.stereotype.Service;
//
//import lombok.RequiredArgsConstructor;
//import vehcon.dto.auth.UsersDTO;
//import vehcon.models.auth.User;
//import vehcon.repo.auth.UserRepository;
//
//@Service
//@RequiredArgsConstructor
//public class UsersService {
//	
//	private UserRepository userRepo;
//	
//	//create new user
//	public User addUsers(UsersDTO dto)
//	{
//		User user = new User();
//		user.setUsername(dto.getUsername());
//		user.setPassword(dto.getPassword());
//		user.setName(dto.getName());
//		user.setDepartmentCode(dto.getDepartmentCode());
//		user.setRole(dto.getRole()); 
//		user.setUserAccess(dto.isUseraccess());
//		return userRepo.save(user);
//	}
//	
//	//update existing user
//}
