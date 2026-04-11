package com.project.shopapp.services.User;

import com.project.shopapp.dtos.UpdateUserDTO;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidPasswordException;
import com.project.shopapp.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
//    String login(String phoneNumber, String password, Long roleId) throws Exception;
    String login(String phoneNumber, String password) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    User updatedUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;
    Page<User> findAll(String keyword, Pageable pageable) ;
    User getUserById(Long userId) throws Exception;
    void resetPassword(Long userId, String newPassword)
            throws InvalidPasswordException, DataNotFoundException ;

    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException;


}
