package com.example.services.auth;

import com.example.dao.user.UserDao;
import com.example.dto.user.SignUpDto;
import com.example.dto.user.UserDetailsImpl;
import com.example.dto.user.UserDto;
import com.example.jpa.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserDao userDao;

    @Autowired
    public UserDetailsServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return userToUserDetailsImpl(user);
    }

    public UserDto getUserById(Long id) {
        User user = userDao.findById(id).orElseThrow(() -> new RuntimeException("User not found: " + id));

        return userToUserDto(user);
    }

    private UserDetailsImpl userToUserDetailsImpl(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setId(user.getId());
        userDetails.setEmail(user.getEmail());
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(user.getPassword());
        userDetails.setRole(user.getRole().name());

        return userDetails;
    }

    private UserDto userToUserDto(User user) {
        UserDto userDetails = new UserDto();
        userDetails.setId(user.getId());
        userDetails.setEmail(user.getEmail());
        userDetails.setUsername(user.getUsername());
        userDetails.setRole(user.getRole().name());

        return userDetails;
    }

    public UserDetails addUser(SignUpDto signUpDto) {
        if (userDao.existsByEmail(signUpDto.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }

        if (userDao.existsByUsername(signUpDto.getUsername())) {
            throw new RuntimeException("User with this username already exists");
        }

        User user = userDao.save(signUpDtoToUser(signUpDto));
        return userToUserDetails(user);
    }

    private User signUpDtoToUser(SignUpDto signUpDto) {
        User user = new User();
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(signUpDto.getPassword());
        user.setRole(signUpDto.getRole());

        return user;
    }

    private UserDetailsImpl userToUserDetails(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setUsername(user.getUsername());
        userDetails.setEmail(user.getEmail());
        userDetails.setPassword(user.getPassword());
        userDetails.setRole(user.getRole().name());
        userDetails.setId(user.getId());

        return userDetails;
    }
}
