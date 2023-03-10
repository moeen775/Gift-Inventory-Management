package com.square.Inventory.Management.System.ServiceImpl;

import com.square.Inventory.Management.System.Constant.InventoryConstant;
import com.square.Inventory.Management.System.DTO.UserDTO;
import com.square.Inventory.Management.System.Entity.LogInDetails;
import com.square.Inventory.Management.System.Entity.User;
import com.square.Inventory.Management.System.IMSUtils.EmailUtils;
import com.square.Inventory.Management.System.IMSUtils.InventoryUtils;
import com.square.Inventory.Management.System.IMSUtils.OtpUtils;
import com.square.Inventory.Management.System.JWT.CustomUserServiceDetails;
import com.square.Inventory.Management.System.JWT.JWTFilter;
import com.square.Inventory.Management.System.JWT.JWTUtils;
import com.square.Inventory.Management.System.Projection.ActivatedDeactivatedUser;
import com.square.Inventory.Management.System.Repository.LogInHistoryRepository;
import com.square.Inventory.Management.System.Repository.UserRepository;
import com.square.Inventory.Management.System.Service.UserService;
import com.square.Inventory.Management.System.Validator.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final CustomUserServiceDetails customUserServiceDetails;

    private final JWTUtils jwtUtils;

    private final JWTFilter jwtFilter;

    private final EmailUtils emailUtils;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final OtpUtils otpUtils;

    private final LogInHistoryRepository logInHistoryRepository;

    public UserServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager, CustomUserServiceDetails customUserServiceDetails, JWTUtils jwtUtils, JWTFilter jwtFilter, EmailUtils emailUtils, BCryptPasswordEncoder bCryptPasswordEncoder, OtpUtils otpUtils, LogInHistoryRepository logInHistoryRepository) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.customUserServiceDetails = customUserServiceDetails;
        this.jwtUtils = jwtUtils;
        this.jwtFilter = jwtFilter;
        this.emailUtils = emailUtils;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.otpUtils = otpUtils;
        this.logInHistoryRepository = logInHistoryRepository;
    }

    @Override
    public ResponseEntity<?> createUser(UserDTO user) {
        try {
            User newUser = userRepository.findByEmail(user.getEmail());
            if (Objects.isNull(newUser)) {

                userRepository.save(getUserFromDTO(user));
                emailUtils.sendMail(user.getEmail(), "Account Approved By" + " " + getCurrentUserName(), "Email: " + user.getEmail() + "\n" + "Password " + user.getPassword() + "\n" + "Please Change Your Password As Soon As possible http//:localhost:8080/inventory/user/changePassword" + "\n" + "Thank You!!!" + "\n" + "\n" + "This mail Send from IMS by Square");
                return new ResponseEntity<>(HttpStatus.CREATED);

            } else {
                return new ResponseEntity<>(HttpStatus.FOUND);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getCurrentUserName() {

        User user = userRepository.findByEmail(jwtFilter.getCurrentUser());
        return user.getFirstName() + "  " + user.getLastName();
    }

    @Override
    public ResponseEntity<?> login(UserDTO userDTO) {
        try {
            User user = userRepository.findByEmail(userDTO.getEmail());
            if (Objects.nonNull(user)) {
                Authentication auth = authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword()));
                if (auth.isAuthenticated()) {
                    if (customUserServiceDetails.getUserDetails().getStatus().equalsIgnoreCase("active")||
                    customUserServiceDetails.getUserDetails().getStatus().equalsIgnoreCase("true")) {
                        logInHistoryRepository.save(getLogInDetails(userDTO));
                        return new ResponseEntity<>("{\"token\":\"" + jwtUtils.generateToken(customUserServiceDetails.getUserDetails().getEmail(), customUserServiceDetails.getUserDetails().getRole()) + "\"}", HttpStatus.OK);
                    } else {
                        logInHistoryRepository.save(getLogInDetails(userDTO));
                        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
                    }
                }
            } else {
                logInHistoryRepository.save(getLogInDetails(userDTO));
                return new ResponseEntity<>( HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        logInHistoryRepository.save(getFailureLogIn(userDTO));
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private LogInDetails getFailureLogIn(UserDTO userDTO) {
        LogInDetails logInDetails = new LogInDetails();
        InetAddress inetAddress = InetAddress.getLoopbackAddress();
        logInDetails.setLogInTime(new Date());
        logInDetails.setUserEmail(userDTO.getEmail());
        logInDetails.setLogInStatus("failed log in");
        logInDetails.setDeviceIP(inetAddress.getHostAddress());
        return logInDetails;
    }


    private LogInDetails getLogInDetails(UserDTO userDTO) throws UnknownHostException {

        LogInDetails logInDetails = new LogInDetails();
        InetAddress inetAddress = InetAddress.getLocalHost();
        User user = userRepository.findByEmail(userDTO.getEmail());

        if (Objects.nonNull(user) && user.getStatus().equals("active")) {
            logInDetails.setUserId(user.getId());
            logInDetails.setLogInStatus("success");
        } else if (Objects.isNull(user)) {
            logInDetails.setLogInStatus("No account");
        } else {
            logInDetails.setLogInStatus("Disable account");
        }

        logInDetails.setUserEmail(userDTO.getEmail());
        logInDetails.setLogInTime(new Date());
        logInDetails.setDeviceIP(inetAddress.getHostAddress());
        return logInDetails;
    }

    @Override
    public ResponseEntity<?> update(UserDTO user, Long userId) {
        try {
            Optional<User> user1 = userRepository.findById(userId);
            if (user1.isPresent()) {
                User user2 = user1.get();
                user2.setFirstName(user.getFirstName());
                user2.setLastName(user.getLastName());
                user2.setContactNumber(user.getContactNumber());
                userRepository.save(user2);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> updateUserRole(String role, Long userID) {
        try {
            Optional<User> user1 = userRepository.findById(userID);
            if (user1.isPresent()) {
                User user2 = user1.get();
                user2.setRole(role);
                userRepository.save(user2);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> updateUserStatus(String status, Long userID) {
        try {
            Optional<User> user = userRepository.findById(userID);
            if (user.isPresent()) {
                User newUser = user.get();
                newUser.setStatus(status);
                userRepository.save(newUser);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public List<String> getClaimDetails() {
        List<String> stringList = new ArrayList<>();
        stringList.add(jwtFilter.getCurrentUser());
        stringList.add(jwtFilter.getRole());
        return stringList;
    }

    @Override
    public Object getClaimFromLogin() {
        User user = userRepository.findByEmail(jwtFilter.getCurrentUser());

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setContactNumber(user.getContactNumber());
        userDTO.setRole(user.getRole());
        return userDTO;
    }

    @Override
    public ResponseEntity<String> disableUser(Long userId) {

        Optional<User> user = Optional.ofNullable(userRepository.findById(userId))
                .orElseThrow(() -> new NotFoundException("User " + userId + "is not present"));

        if (!"admin".equals(user.get().getRole())) {

            userRepository.disableUser(userId);
            if (Objects.equals(user.get().getStatus(), "active")) {
                return new ResponseEntity<>("User Disable Successfully.ID:  " + userId, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User Active Successfully.ID:  " + userId, HttpStatus.OK);
            }

        } else {
            return new ResponseEntity<>( HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.getAllUsers();
    }

//    @Override
//    public List<UserDTO> getAllUserByPagination(int page, int size) {
//        Pageable paging = PageRequest.of(page, size);
//        Page<UserDTO> pageResult = userRepository.getAllUser(paging);
//        if (pageResult.hasContent()) {
//            return pageResult.getContent();
//        } else {
//            return new ArrayList<>();
//        }
//    }

    @Override
    public List<UserDTO> getAllUserByPaginationBySort(int page, int size, String sortBy) {
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        Page<UserDTO> pageResult = userRepository.getAllUser(paging);
        if (pageResult.hasContent()) {
            return pageResult.getContent();
        } else {
            return new ArrayList<>();
        }
    }

    private User getUserFromDTO(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setContactNumber(userDTO.getContactNumber());
        user.setEmail(userDTO.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole());
        user.setStatus("active");
        return user;
    }

    @Override
    public ResponseEntity<?> forgetPassword(User user) {
        String otp = otpUtils.generateOTP(user);
        String email = user.getEmail();

        user.setOtp(otp);
        user.setSetOtpGenerationTime(new Date());
        userRepository.save(user);

        emailUtils.sendMail(email, "Forget Password Request", "Hello User,\n" + "Your OTP is: " + otp + "\n" + "\n" + "This mail Send from IMS by Square\n" + "Note: this OTP is set to expire in 5 minutes.");
        return ResponseEntity.ok("OTP generated!! check mail");
    }

    @Override
    public Boolean checkOtpStatus(User user, String givenOtp) {
        Optional<User> _user = Optional.ofNullable(userRepository.findByOtp(givenOtp));

        if (_user.isEmpty() ) return false;
        return user.getOtp().equals(givenOtp) && !otpUtils.isOtpExpired(user);
    }

    @Override
    public ResponseEntity<?> resetPassword(User user, String newPassword, String givenOtp) {
        Boolean checkOtpStatus = checkOtpStatus(user, givenOtp);
        if (!checkOtpStatus) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Otp is not valid");
        }

        newPassword = bCryptPasswordEncoder.encode(newPassword);
        String oldPassword = user.getPassword();

        if (Objects.equals(newPassword, oldPassword)) {
            return (ResponseEntity<?>) ResponseEntity.badRequest();
        } else {
            user.setPassword(newPassword);
            otpUtils.clearOTP(user);
            return ResponseEntity.ok("Password Updated!");
        }
    }

    @Override
    public ResponseEntity<ActivatedDeactivatedUser> getActiveDeactivateUser() {
        ActivatedDeactivatedUser activatedDeactivatedUsers = userRepository.getActiveDeactivateUser();
        return new ResponseEntity<>(activatedDeactivatedUsers, HttpStatus.OK);

    }

}