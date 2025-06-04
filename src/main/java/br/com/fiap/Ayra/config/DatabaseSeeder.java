package br.com.fiap.Ayra.config;

import br.com.fiap.Ayra.model.*;
import br.com.fiap.Ayra.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Configuration
public class DatabaseSeeder {

    @Autowired
    private CoordinatesRepository coordinatesRepository;

    @Autowired
    private MapMarkerRepository mapMarkerRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private SafeRoutesRepository safeRoutesRepository;

    @Autowired
    private SafeLocationRepository safeLocationRepository;

    @Autowired
    private SafeTipRepository safeTipsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // 1. Coordenadas (Locais reais no estado de São Paulo)
        Coordinates saoPauloCenter = Coordinates.builder()
                .latitude(-23.5505) // Centro de São Paulo
                .longitude(-46.6333)
                .dateCoordinate(LocalDate.now().minusDays(1))
                .build();

        Coordinates santana = Coordinates.builder()
                .latitude(-23.4800) // Santana (~10 km ao norte do centro)
                .longitude(-46.6300)
                .dateCoordinate(LocalDate.now())
                .build();

        Coordinates moema = Coordinates.builder()
                .latitude(-23.6000) // Moema (~12 km ao sul do centro)
                .longitude(-46.6700)
                .dateCoordinate(LocalDate.now().minusDays(2))
                .build();

        Coordinates barueri = Coordinates.builder()
                .latitude(-23.5100) // Barueri (~15 km a oeste do centro)
                .longitude(-46.8800)
                .dateCoordinate(LocalDate.now().minusDays(3))
                .build();

        Coordinates guarulhos = Coordinates.builder()
                .latitude(-23.4600) // Guarulhos (~13 km a nordeste do centro)
                .longitude(-46.5300)
                .dateCoordinate(LocalDate.now().minusDays(4))
                .build();

        coordinatesRepository.save(saoPauloCenter);
        coordinatesRepository.save(santana);
        coordinatesRepository.save(moema);
        coordinatesRepository.save(barueri);
        coordinatesRepository.save(guarulhos);

        // 2. MapMarker (Marcadores relacionados aos locais acima)
        MapMarker marker1 = MapMarker.builder()
                .title("Inundação no Centro")
                .description("Água acumulada nas ruas do centro após chuvas fortes.")
                .intensity("high")
                .radius(99.99)
                .coordinates(saoPauloCenter)
                .build();

        MapMarker marker2 = MapMarker.builder()
                .title("Alagamento em Santana")
                .description("Alagamento em vias secundárias de Santana após chuvas intensas.")
                .intensity("medium")
                .radius(80.0)
                .coordinates(santana)
                .build();

        MapMarker marker3 = MapMarker.builder()
                .title("Erosão em Moema")
                .description("Erosão severa em área urbana de Moema próxima a córregos.")
                .intensity("high")
                .radius(70.0)
                .coordinates(moema)
                .build();

        MapMarker marker4 = MapMarker.builder()
                .title("Inundação Leve em Barueri")
                .description("Água acumulada em áreas baixas de Barueri após chuvas fortes.")
                .intensity("low")
                .radius(50.0)
                .coordinates(barueri)
                .build();

        MapMarker marker5 = MapMarker.builder()
                .title("Área Segura em Guarulhos")
                .description("Abrigo seguro localizado em Guarulhos.")
                .intensity("low")
                .radius(30.0)
                .coordinates(guarulhos)
                .build();

        mapMarkerRepository.save(marker1);
        mapMarkerRepository.save(marker2);
        mapMarkerRepository.save(marker3);
        mapMarkerRepository.save(marker4);
        mapMarkerRepository.save(marker5);

        // 3. Alertas (Relacionados aos marcadores acima)
        Alert alert1 = Alert.builder()
                .title("Inundação Severa no Centro")
                .description("Inundação severa próxima ao rio Tietê com risco para moradores no centro de São Paulo.")
                .intensity("high")
                .alertDatetime(ZonedDateTime.now().minusHours(2))
                .location("Centro de São Paulo")
                .radius(99.99)
                .coordinates(saoPauloCenter)
                .mapMarker(marker1)
                .build();

        Alert alert2 = Alert.builder()
                .title("Alagamento em Santana")
                .description("Alagamento em vias secundárias de Santana após chuvas intensas.")
                .intensity("medium")
                .alertDatetime(ZonedDateTime.now().minusHours(5))
                .location("Santana, São Paulo")
                .radius(80.0)
                .coordinates(santana)
                .mapMarker(marker2)
                .build();

        Alert alert3 = Alert.builder()
                .title("Erosão Urbana em Moema")
                .description("Erosão severa em área urbana de Moema próxima a córregos.")
                .intensity("high")
                .alertDatetime(ZonedDateTime.now().minusHours(10))
                .location("Moema, São Paulo")
                .radius(70.0)
                .coordinates(moema)
                .mapMarker(marker3)
                .build();

        Alert alert4 = Alert.builder()
                .title("Inundação Leve em Barueri")
                .description("Água acumulada em áreas baixas de Barueri após chuvas fortes.")
                .intensity("low")
                .alertDatetime(ZonedDateTime.now().minusHours(15))
                .location("Barueri, São Paulo")
                .radius(50.0)
                .coordinates(barueri)
                .mapMarker(marker4)
                .build();

        Alert alert5 = Alert.builder()
                .title("Área Segura em Guarulhos")
                .description("Abrigo seguro localizado em Guarulhos.")
                .intensity("low")
                .alertDatetime(ZonedDateTime.now().minusHours(20))
                .location("Guarulhos, São Paulo")
                .radius(30.0)
                .coordinates(guarulhos)
                .mapMarker(marker5)
                .build();

        alertRepository.save(alert1);
        alertRepository.save(alert2);
        alertRepository.save(alert3);
        alertRepository.save(alert4);
        alertRepository.save(alert5);

        // 4. SafeRoutes (Rotas seguras nos locais acima)
        SafeRoutes safeRoute1 = SafeRoutes.builder()
                .route("Rua Direita -> Avenida Paulista -> Praça da Sé")
                .alert(alert1)
                .build();

        SafeRoutes safeRoute2 = SafeRoutes.builder()
                .route("Avenida Braz Leme -> Rua Voluntários da Pátria")
                .alert(alert2)
                .build();

        SafeRoutes safeRoute3 = SafeRoutes.builder()
                .route("Avenida Ibirapuera -> Rua dos Otonis")
                .alert(alert3)
                .build();

        SafeRoutes safeRoute4 = SafeRoutes.builder()
                .route("Rodovia Castelo Branco -> Alphaville")
                .alert(alert4)
                .build();

        SafeRoutes safeRoute5 = SafeRoutes.builder()
                .route("Rodovia Presidente Dutra -> Centro de Guarulhos")
                .alert(alert5)
                .build();

        safeRoutesRepository.save(safeRoute1);
        safeRoutesRepository.save(safeRoute2);
        safeRoutesRepository.save(safeRoute3);
        safeRoutesRepository.save(safeRoute4);
        safeRoutesRepository.save(safeRoute5);

        // 5. SafeLocation (Locais seguros nos locais acima)
        SafeLocation safeLocation1 = SafeLocation.builder()
                .location("Praça da Sé")
                .alert(alert1)
                .build();

        SafeLocation safeLocation2 = SafeLocation.builder()
                .location("Parque da Juventude")
                .alert(alert2)
                .build();

        SafeLocation safeLocation3 = SafeLocation.builder()
                .location("Parque Ibirapuera")
                .alert(alert3)
                .build();

        SafeLocation safeLocation4 = SafeLocation.builder()
                .location("Alphaville Tênis Clube")
                .alert(alert4)
                .build();

        SafeLocation safeLocation5 = SafeLocation.builder()
                .location("Prefeitura de Guarulhos")
                .alert(alert5)
                .build();

        safeLocationRepository.save(safeLocation1);
        safeLocationRepository.save(safeLocation2);
        safeLocationRepository.save(safeLocation3);
        safeLocationRepository.save(safeLocation4);
        safeLocationRepository.save(safeLocation5);

        // 6. SafeTips (Dicas de segurança nos locais acima)
        SafeTip safeTip1 = SafeTip.builder()
                .tip("Evite áreas próximas ao rio Tietê em dias de chuva forte.")
                .alert(alert1)
                .build();

        SafeTip safeTip2 = SafeTip.builder()
                .tip("Não dirija por vias alagadas após chuvas intensas.")
                .alert(alert2)
                .build();

        SafeTip safeTip3 = SafeTip.builder()
                .tip("Mantenha-se longe de córregos e áreas com erosão visível.")
                .alert(alert3)
                .build();

        SafeTip safeTip4 = SafeTip.builder()
                .tip("Use rotas alternativas em caso de alagamentos.")
                .alert(alert4)
                .build();

        SafeTip safeTip5 = SafeTip.builder()
                .tip("Procure abrigos designados em emergências.")
                .alert(alert5)
                .build();

        safeTipsRepository.save(safeTip1);
        safeTipsRepository.save(safeTip2);
        safeTipsRepository.save(safeTip3);
        safeTipsRepository.save(safeTip4);
        safeTipsRepository.save(safeTip5);

        // 7. User com senha criptografada
        User user1 = User.builder()
                .name("João Silva")
                .email("joao@example.com")
                .password(passwordEncoder.encode("senha123"))
                .phone("11999999999")
                .coordinates(saoPauloCenter)
                .build();

        User user2 = User.builder()
                .name("Maria Souza")
                .email("maria@example.com")
                .password(passwordEncoder.encode("senha456"))
                .phone("11888888888")
                .coordinates(santana)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        System.out.println("Database seeded with sample data!");
    }
}