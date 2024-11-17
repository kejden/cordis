package io.ndk.cordis_backend.service.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.request.AccountSignUp;
import io.ndk.cordis_backend.dto.request.SignInRequest;
import io.ndk.cordis_backend.dto.response.SignInResponse;
import io.ndk.cordis_backend.entity.UserEntity;
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


    @Override
    public Optional<AccountSignUp> signUp(AccountSignUp dto) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(dto.getEmail());
        if(userEntity.isPresent()){
            return Optional.empty();
        }
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        UserEntity user = mapper.mapFrom(dto); // TODO maper nie mapuje emaila ?
        user.setEmail(dto.getEmail());
        UserEntity savedUser = userRepository.save(user);
        return Optional.of(mapper.mapTo(savedUser));

    }

    @Override
    public Optional<SignInResponse> signIn(SignInRequest dto) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(dto.getEmail());
        if(userEntity.isEmpty()){
            return Optional.empty();
        }
        UserEntity user = userEntity.get();
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        if (authentication.isAuthenticated()) {
            return Optional.of(
                    SignInResponse.builder()
                            .id(user.getId())
                            .userName(user.getUserName())
                            .email(dto.getEmail())
                            .accessToken(jwtService.generateToken(dto.getEmail()))
                            .build()
            );
        } else {
            return Optional.empty();
        }
    }
}
