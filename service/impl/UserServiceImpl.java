package com.ecom.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepo;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public UserDtls saveUser(UserDtls user) {
		user.setRole("ROLE_USER");
		user.setIsEnable(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		
		UserDtls saveUser = userRepo.save(user);
		
		return saveUser;
	}

	@Override
	public UserDtls getUserByEmail(String email) {
		
		return userRepo.findByEmail(email);
	}

	@Override
	public List<UserDtls> getUsers(String role) {
		
		
		return userRepo.findByRole(role);
	}

	@Override
	public Boolean updateAccountStatus(Integer id, Boolean status) {
		
		Optional<UserDtls> findByuser = userRepo.findById(id);
		
		if (findByuser.isPresent()) {
			
			UserDtls userDtls = findByuser.get();
			
			userDtls.setIsEnable(status);
			
			userRepo.save(userDtls);
			
			return true;
		}
		
		return false;
	}

	@Override
	public void increaseFailedAttempt(UserDtls user) {
		int attempt = user.getFailedAttempt() + 1;
		user.setFailedAttempt(attempt);
		userRepo.save(user);
		
	}

	@Override
	public void userAccountLock(UserDtls user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new Date());
		userRepo.save(user);
	}

	@Override
	public boolean unlockAccountTimeExpired(UserDtls user) {
		
		long lockTime = user.getLockTime().getTime();
		long unlockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
		
		long currentTime = System.currentTimeMillis();
		
		if (unlockTime<currentTime) {
		
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
			userRepo.save(user);
			
			return true;
		}
		
		
		return false;
	}

	@Override
	public void resetAttempt(int userId) {
		
		
	}

	@Override
	public void updateUserResetToken(String email, String resetToken) {
		UserDtls findByEmail = userRepo.findByEmail(email);
		findByEmail.setResetToken(resetToken);
		userRepo.save(findByEmail);
	}

	@Override
	public UserDtls getUserByToken(String token) {
		
		
		return userRepo.findByResetToken(token);
	}

	@Override
	public UserDtls updateUser(UserDtls user) {


		
		return userRepo.save(user);
	}

	@Override
	public UserDtls updateUserProfile(UserDtls user,MultipartFile img) {
		
		UserDtls dbUser = userRepo.findById(user.getId()).get();
		
		if (!img.isEmpty()) {
			dbUser.setProfileImage(img.getOriginalFilename());
		}

		if (!ObjectUtils.isEmpty(dbUser)) {

			dbUser.setName(user.getName());
			dbUser.setMobileNumber(user.getMobileNumber());
			dbUser.setAddress(user.getAddress());
			dbUser.setCity(user.getCity());
			dbUser.setState(user.getState());
			dbUser.setPincode(user.getPincode());
			dbUser = userRepo.save(dbUser);
		}
		
		
		try {
		if (!img.isEmpty()) {
			
			File saveFile = new ClassPathResource("static/images").getFile();
			
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"profile_img"+File.separator+img.getOriginalFilename());
			
			
			/* System.out.println(path); */
			
			Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public UserDtls saveAdmin(UserDtls user) {
		user.setRole("ROLE_ADMIN");
		user.setIsEnable(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);

		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		UserDtls saveUser = userRepo.save(user);
		return saveUser;
	}

	@Override
	public Boolean existsEmail(String email) {
		
		return userRepo.existsByEmail(email);
	}

}