package com.novabank;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModulithArchitectureTest {

    private final ApplicationModules modules = ApplicationModules.of(NovaBankApplication.class);

    @Test
    void shouldHaveAcyclicModuleGraph() {
        modules.verify();
    }

    @Test
    void shouldWriteModuleDocumentation() {
        new Documenter(modules).writeDocumentation();
    }
}
