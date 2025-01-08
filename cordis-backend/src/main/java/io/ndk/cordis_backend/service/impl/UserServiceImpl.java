package io.ndk.cordis_backend.service.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.UserDto;
import io.ndk.cordis_backend.dto.request.AccountSignUp;
import io.ndk.cordis_backend.dto.request.EditUserRequest;
import io.ndk.cordis_backend.dto.request.SignInRequest;
import io.ndk.cordis_backend.dto.response.SignInResponse;
import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.enums.UserStatus;
import io.ndk.cordis_backend.handler.BusinessErrorCodes;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.repository.UserRepository;
import io.ndk.cordis_backend.service.FileService;
import io.ndk.cordis_backend.service.JwtService;
import io.ndk.cordis_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Mapper<UserEntity, AccountSignUp> mapper;
    private final Mapper<UserEntity, UserDto> userMapper;
    private final FileService fileService;

    @Value("${application.file.cdn}")
    private String cdnBaseUrl;

    @Override
    public AccountSignUp signUp(AccountSignUp dto) {
        userRepository.findByEmail(dto.getEmail()).ifPresent(user -> {throw new CustomException(BusinessErrorCodes.EMAIL_IS_USED);});
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        UserEntity user = UserEntity.builder()
                .password(dto.getPassword())
                .email(dto.getEmail())
                .userName(dto.getUserName())
                .status(UserStatus.OFFLINE)
                .profileImage(fileService.getDefault())
                .build();
        UserEntity savedUser = userRepository.save(user);
        return mapper.mapTo(savedUser);

    }

    @Override
    public SignInResponse signIn(SignInRequest dto) {
        UserEntity user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL));
//        if(!user.isEnabled())
//            throw new MessagingException("account is not active");
//
//        if(user.isAccountLocked())
//            throw new MessagingException("account is locked");
        user.setStatus(UserStatus.ONLINE);
        userRepository.save(user);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        if (authentication.isAuthenticated()) {
            return
                    SignInResponse.builder()
                            .id(user.getId())
                            .userName(user.getUserName())
                            .email(dto.getEmail())
                            .profileImage(user.getProfileImage())
                            .build();
        } else {
            throw new CustomException(BusinessErrorCodes.BAD_CREDENTIALS);
        }
    }

    @Override
    public void logout(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL));
        user.setStatus(UserStatus.OFFLINE);
        userRepository.save(user);
    }

    @Override
    public UserDto updateUser(String email, UserDto dto) {
        return userMapper.mapTo(userRepository.findByEmail(email).map(existingUser -> {
                    Optional.ofNullable(dto.getUserName()).ifPresent(existingUser::setUserName);
                    Optional.ofNullable(dto.getProfileImage()).ifPresent(existingUser::setProfileImage);
                    return userRepository.save(existingUser);
            }).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL)
        ));
    }

    @Override
    public UserDto editUser(EditUserRequest request, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL));
        user.setUserName(request.getUsername());
        UserEntity save = userRepository.save(user);

        return userMapper.mapTo(save);
    }

    @Override
    public String updateUserImageProfile(MultipartFile file, Principal principal) {
        UserEntity user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL));

        String imagePath = fileService.updateFile(file, user.getProfileImage());
        user.setProfileImage(imagePath);
        userRepository.save(user);
        return cdnBaseUrl+imagePath;
    }


}
