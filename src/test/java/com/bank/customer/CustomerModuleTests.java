package com.bank.customer;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;

/**
 * Customer Module Isolated Test
 *
 * @ApplicationModuleTest is a Spring Modulith annotation that:
 * 1. Loads ONLY the customer module's Spring beans
 * 2. Does NOT load account, transaction, or any other module
 * 3. Stubs out dependencies on other modules automatically
 *
 * This is the equivalent of a microservice's self-contained test.
 * The customer module is tested in complete isolation.
 *
 * Compare with @SpringBootTest:
 * - @SpringBootTest: loads ALL beans, tests the whole application
 * - @ApplicationModuleTest: loads ONLY this module's beans
 *
 * Benefits:
 * - Faster test execution (smaller Spring context)
 * - True module isolation — failures point to the module, not integrations
 * - Forces you to think about module boundaries (can't just autowire anything)
 *
 * Note: We need Testcontainers for a real PostgreSQL database.
 * In Phase 3, we'll add Testcontainers setup. For now, this demonstrates
 * the @ApplicationModuleTest concept.
 */
@ApplicationModuleTest
class CustomerModuleTests {

    /*
     * This test class demonstrates the @ApplicationModuleTest concept.
     * Full integration tests with Testcontainers will be added in Phase 3.
     *
     * When you run this test, Spring Modulith will:
     * 1. Detect the 'customer' module from this test's package
     * 2. Load only customer module beans
     * 3. Show you the module's detected structure
     */

    @Test
    void customerModule_isDetectedCorrectly() {
        // This test simply verifies the module loads without errors.
        // The fact that it passes confirms:
        // 1. The customer module's Spring context is valid
        // 2. All required beans are available within the module
        // 3. No missing dependencies within the module
        System.out.println("Customer module loaded successfully in isolation.");
    }
}
