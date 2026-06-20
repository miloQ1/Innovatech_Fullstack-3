package cl.innovatech.authService.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cl.innovatech.authService.DTOs.request.UserCreateRequestDTO;
import cl.innovatech.authService.DTOs.request.UserUpdateRequestDTO;
import cl.innovatech.authService.DTOs.response.UserResponseDTO;
import cl.innovatech.authService.model.User;
import cl.innovatech.authService.repository.UserRepository;


@Service
@Transactional
public class UserService {

    private static final Set<String> ALLOWED_STATUSES = Set.of("ACTIVE", "INACTIVE", "LOCKED");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO createUser(UserCreateRequestDTO dto) {
        String userName = dto.getUserName().trim();
        String email = dto.getEmail().trim().toLowerCase();

        if (userRepository.existsByUserName(userName)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El userName ya está registrado");
        }

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }

        User user = new User();
        user.setUserName(userName);
        user.setFirstName(dto.getFirstName().trim());
        user.setLastName(dto.getLastName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setStatus("ACTIVE");
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        return mapToResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(String id) {
        User user = findUserEntityById(id);
        return mapToResponseDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUserName(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return mapToResponseDto(user);
    }

    public UserResponseDTO updateUser(String id, UserUpdateRequestDTO dto) {
        User user = findUserEntityById(id);

        if (dto.getUserName() != null && !dto.getUserName().isBlank()) {
            String newUserName = dto.getUserName().trim();

            if (!newUserName.equals(user.getUserName()) && userRepository.existsByUserName(newUserName)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El userName ya está registrado");
            }

            user.setUserName(newUserName);
        }

        if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
            user.setFirstName(dto.getFirstName().trim());
        }

        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            user.setLastName(dto.getLastName().trim());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            String newEmail = dto.getEmail().trim().toLowerCase();

            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
            }

            user.setEmail(newEmail);
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            user.setStatus(normalizeAndValidateStatus(dto.getStatus()));
        }

        if (dto.getEnabled() != null) {
            user.setEnabled(dto.getEnabled());
        }

        User updatedUser = userRepository.save(user);
        return mapToResponseDto(updatedUser);
    }

    public void deleteUser(String id) {
        User user = findUserEntityById(id);
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public User findUserEntityById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private String normalizeAndValidateStatus(String status) {
        String normalizedStatus = status.trim().toUpperCase();

        if (!ALLOWED_STATUSES.contains(normalizedStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estado inválido. Valores permitidos: ACTIVE, INACTIVE, LOCKED"
            );
        }

        return normalizedStatus;
    }

    private UserResponseDTO mapToResponseDto(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus());
        dto.setEnabled(user.getEnabled());
        dto.setLastLoginAt(user.getLastLoginAt());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
