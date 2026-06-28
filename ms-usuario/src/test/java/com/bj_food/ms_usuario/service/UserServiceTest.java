package com.bj_food.ms_usuario.service;

import com.bj_food.ms_usuario.domain.model.User;
import com.bj_food.ms_usuario.dto.request.CreateUserRequest;
import com.bj_food.ms_usuario.dto.response.UserResponse;
import com.bj_food.ms_usuario.exception.ForbiddenException;
import com.bj_food.ms_usuario.exception.ResourceNotFoundException;
import com.bj_food.ms_usuario.repository.AddressRepository;
import com.bj_food.ms_usuario.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private User user;
    private UUID userId;
    private String keycloakId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        keycloakId = "keycloak-123";

        user = new User();
        user.setKeycloakId(keycloakId);
        user.setName("John Doe");
        user.setEmail("john@email.com");
        user.setPhone("81999990001");
    }


    @Test
    @DisplayName("should create user successfully")
    void createUser_success() {
        CreateUserRequest request = new CreateUserRequest("John Doe", "john@email.com", "81999990001", keycloakId);

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("john@email.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("should throw exception when email already in use")
    void createUser_emailAlreadyInUse() {
        CreateUserRequest request = new CreateUserRequest("John Doe", "john@email.com", "81999990001", keycloakId);

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("E-mail já cadastrado:");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should find user by id when owner")
    void findById_success_owner() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn(keycloakId);
        when(authentication.getAuthorities()).thenReturn(List.of());

        UserResponse response = userService.findById(userId, authentication);

        assertThat(response).isNotNull();
        assertThat(response.keycloakId()).isEqualTo(keycloakId);
    }

    @Test
    @DisplayName("should find user by id when admin")
    void findById_success_admin() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(authentication).getAuthorities();

        UserResponse response = userService.findById(userId, authentication);

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when user not found")
    void findById_notFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(userId, authentication))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("should throw ForbiddenException when accessing another user")
    void findById_forbidden() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn("other-keycloak-id");
        when(authentication.getAuthorities()).thenReturn(List.of());

        assertThatThrownBy(() -> userService.findById(userId, authentication))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Acesso negado");
    }

    @Test
    @DisplayName("should update user successfully")
    void updateUser_success() {
        CreateUserRequest request = new CreateUserRequest("John Updated", "john@email.com", "81999990002", keycloakId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn(keycloakId);
        when(authentication.getAuthorities()).thenReturn(List.of());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.update(userId, request, authentication);

        assertThat(response).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("should throw ForbiddenException when updating another user")
    void updateUser_forbidden() {
        CreateUserRequest request = new CreateUserRequest("John Updated", "john@email.com", "81999990002", keycloakId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn("other-keycloak-id");
        when(authentication.getAuthorities()).thenReturn(List.of());

        assertThatThrownBy(() -> userService.update(userId, request, authentication))
                .isInstanceOf(ForbiddenException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should deactivate user successfully")
    void deactivateUser_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn(keycloakId);
        when(authentication.getAuthorities()).thenReturn(List.of());
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.deactivate(userId, authentication);

        assertThat(user.getActive()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("should throw ForbiddenException when deactivating another user")
    void deactivateUser_forbidden() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn("other-keycloak-id");
        when(authentication.getAuthorities()).thenReturn(List.of());

        assertThatThrownBy(() -> userService.deactivate(userId, authentication))
                .isInstanceOf(ForbiddenException.class);

        verify(userRepository, never()).save(any());
    }
}