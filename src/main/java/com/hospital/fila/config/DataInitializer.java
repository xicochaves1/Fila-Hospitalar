package com.hospital.fila.config;

import com.hospital.fila.model.Usuario;
import com.hospital.fila.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            criarUsuario("admin", "admin123", "Administrador", "ROLE_ADMIN");
            criarUsuario("atendente1", "senha123", "Ana Atendente", "ROLE_ATENDENTE");
            criarUsuario("medico1", "senha123", "Dr. Carlos Silva", "ROLE_MEDICO");
        }
    }

    private void criarUsuario(String username, String senha, String nome, String role) {
        Usuario u = Usuario.builder()
                .username(username)
                .password(passwordEncoder.encode(senha))
                .nome(nome)
                .role(role)
                .ativo(true)
                .build();
        usuarioRepository.save(u);
    }
}
