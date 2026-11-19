package com.project.apilistadetarefas.config;

import com.project.apilistadetarefas.entity.Priority;
import com.project.apilistadetarefas.entity.enums.PriorityEnum;
import com.project.apilistadetarefas.repository.PriorityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Order(value = 1)
@RequiredArgsConstructor
public class PriorityInitializerConfig implements CommandLineRunner {

    private final PriorityRepository priorityRepository;

    private static final Logger log = LoggerFactory.getLogger(PriorityInitializerConfig.class);
    private static final Map<PriorityEnum, Seed> SEEDS = new HashMap<>();

    private static final Map<PriorityEnum, Seed> buildSeeds() {
        Map<PriorityEnum, Seed> map = new EnumMap<>(PriorityEnum.class);
        map.put(PriorityEnum.LOW,    new Seed("#10B981", 1));   // verde
        map.put(PriorityEnum.MEDIUM, new Seed("#F59E0B", 2));   // âmbar
        map.put(PriorityEnum.HIGH,   new Seed("#F97316", 3));   // laranja
        map.put(PriorityEnum.URGENT, new Seed("#EF4444", 4));

        return Map.copyOf(map);
    }

    @Override
    public void run(String... args) throws Exception {
        int created = 0;
        int updated = 0;
        int skipped = 0;

        for (Map.Entry<PriorityEnum, Seed> entry : SEEDS.entrySet()) {
            PriorityEnum name = entry.getKey();
            Seed seed = entry.getValue();

            Optional<Priority> existing = priorityRepository.findByName(name);

            if (existing.isPresent()) {
                priorityRepository.save(Priority.builder()
                        .name(name)
                        .color(seed.color)
                        .displayOrder(seed.displayOrder)
                        .build());

                created++;
                log.debug("Priority criada: {} ({})", name, seed.color);
                continue;
            }

            Priority priority = existing.get();
            boolean colorChanged = !priority.getColor().equals(seed.color);
            boolean orderChanged = priority.getDisplayOrder() != seed.displayOrder;

            if (colorChanged || orderChanged) {
                priority.setColor(seed.color);
                priority.setDisplayOrder(seed.displayOrder);
                priorityRepository.save(priority);
                updated++;
                log.debug("Priority atualizada: {} ({})", name, seed.color);
                continue;
            } else {
                skipped++;
            }
        }

        if (created + updated > 0) {
            log.info("Priority initializer: {} criada(s), {} atualizada(s), {} sem alteração",
                    created, updated, skipped);
        } else {
            log.debug("Priority initializer: seed em sincronia ({} registros)", skipped);
        }
    }

    private record Seed(String color, int displayOrder) {}
}
