package io.ndk.cordis_backend.service.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.request.AccountSignUp;
import io.ndk.cordis_backend.dto.request.SignInRequest;
import io.ndk.cordis_backend.dto.response.SignInResponse;
import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.enums.UserStatus;
import io.ndk.cordis_backend.handler.BusinessErrorCodes;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.repository.UserRepository;
import io.ndk.cordis_backend.service.JwtService;
import io.ndk.cordis_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Mapper<UserEntity, AccountSignUp> mapper;


    @Override
    public AccountSignUp signUp(AccountSignUp dto) {
        userRepository.findByEmail(dto.getEmail()).ifPresent(user -> {throw new CustomException(BusinessErrorCodes.EMAIL_IS_USED);});
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        UserEntity user = UserEntity.builder()
                .password(dto.getPassword())
                .email(dto.getEmail())
                .userName(dto.getUserName())
                .status(UserStatus.OFFLINE)
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
}
