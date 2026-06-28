package com.bj_food.ms_usuario.service;

import com.bj_food.ms_usuario.domain.model.Address;
import com.bj_food.ms_usuario.domain.model.User;
import com.bj_food.ms_usuario.dto.request.CreateAddressRequest;
import com.bj_food.ms_usuario.dto.request.CreateUserRequest;
import com.bj_food.ms_usuario.dto.response.AddressResponse;
import com.bj_food.ms_usuario.dto.response.UserResponse;
import com.bj_food.ms_usuario.exception.ForbiddenException;
import com.bj_food.ms_usuario.exception.ResourceNotFoundException;
import com.bj_food.ms_usuario.repository.AddressRepository;
import com.bj_food.ms_usuario.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public UserService(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        log.info("Creating user with email={}", request.email());
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Email already in use: {}", request.email());
            throw new IllegalArgumentException("E-mail já cadastrado: " + request.email());
        }
        User user = new User();
        user.setKeycloakId(request.keycloakId());
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhone(request.phone());

        UserResponse response = UserResponse.from(userRepository.save(user));
        log.info("User created successfully id={} email={}", response.id(), response.email());
        return response;
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id, Authentication authentication) {
        log.debug("Finding user by id={}", id);
        User user = findUserById(id);
        validateOwnership(user, authentication);
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse findByKeycloakId(String keycloakId, Authentication authentication) {
        log.debug("Finding user by keycloakId={}", keycloakId);
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + keycloakId));
        validateOwnership(user, authentication);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse update(UUID id, CreateUserRequest request, Authentication authentication) {
        User user = findUserById(id);
        validateOwnership(user, authentication);
        user.setName(request.name());
        user.setPhone(request.phone());

        UserResponse response = UserResponse.from(userRepository.save(user));
        log.info("User updated successfully id={}", id);
        return response;
    }

    @Transactional
    public void deactivate(UUID id, Authentication authentication) {
        User user = findUserById(id);
        validateOwnership(user, authentication);
        user.setActive(false);
        userRepository.save(user);
        log.info("User deactivated successfully id={}", id);
    }

    @Transactional
    public AddressResponse addAddress(UUID userId, CreateAddressRequest request, Authentication authentication) {
        User user = findUserById(userId);
        log.info("Adding address for userId={}", userId);
        validateOwnership(user, authentication);

        if (request.main() && addressRepository.existsByUserIdAndMainTrue(userId)) {
            log.debug("Removing main flag from existing addresses for userId={}", userId);
            addressRepository.findByUserId(userId)
                    .forEach(a -> a.setMain(false));
        }

        Address address = new Address();
        address.setUser(user);
        address.setStreet(request.street());
        address.setNumber(request.number());
        address.setComplement(request.complement());
        address.setNeighborhood(request.neighborhood());
        address.setCity(request.city());
        address.setState(request.state());
        address.setZipCode(request.zipCode());
        address.setMain(request.main());

        AddressResponse response = AddressResponse.from(addressRepository.save(address));
        log.info("Address added successfully addressId={} userId={}", response.id(), userId);
        return response;
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> findAddresses(UUID userId, Authentication authentication) {
        log.debug("Finding addresses for userId={}", userId);
        User user = findUserById(userId);
        validateOwnership(user, authentication);
        return addressRepository.findByUserId(userId)
                .stream().map(AddressResponse::from).toList();
    }

    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }

    private void validateOwnership(User user, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin){
            log.debug("Admin access granted for user id={}", user.getId());
            return;
        }

        String keycloakId = authentication.getName();
        if (!user.getKeycloakId().equals(keycloakId)) {
            log.warn("Access denied: keycloakId={} tried to access userId={}", keycloakId, user.getId());
            throw new ForbiddenException("Acesso negado");
        }
    }
}