package com.bank;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Spring Modulith Architecture Verification Tests
 *
 * This is one of the most important test classes in the project.
 * It verifies that our module boundaries are actually enforced.
 *
 * ApplicationModules.of(BankingApplication.class) scans the entire
 * application from the root package and builds a model of:
 * - All detected modules (customer, account, common, ...)
 * - Their dependencies on each other
 * - Which types are public vs internal
 *
 * The verify() method then checks:
 * 1. No module accesses another module's 'internal' package
 * 2. No cyclic dependencies exist between modules
 * 3. All module dependencies are explicitly declared (if using named interfaces)
 *
 * If any of these rules are violated, verify() throws an exception
 * and the test FAILS with a clear, readable message like:
 *
 *   Module 'account' depends on internal type
 *   'com.bank.customer.internal.CustomerRepository' in module 'customer'.
 *
 * Run this test after every significant code change to ensure
 * the architecture stays clean.
 */
class ModularityTests {

    // Build the module model once — shared across all tests in this class
    private final ApplicationModules modules =
        ApplicationModules.of(BankingApplication.class);

    @Test
    void applicationModules_verifyBoundaries() {
        /*
         * THE most important architectural test.
         *
         * This single line of code enforces ALL module boundary rules:
         * - No cross-module internal access
         * - No cyclic dependencies
         * - Clean module graph
         *
         * If this passes, our architecture is structurally correct.
         * If this fails, the error message tells you exactly what violated the rule.
         */
        modules.verify();
    }

    @Test
    void applicationModules_printStructure() {
        /*
         * Not a real verification — but prints the detected module structure
         * to the console so you can see what Spring Modulith discovered.
         *
         * Sample output:
         *
         * # Module customer
         * > Logical name: customer
         * > Base package: com.bank.customer
         * > Spring beans:
         *   + com.bank.customer.CustomerController
         *   + com.bank.customer.CustomerService
         * > Internal types:
         *   o com.bank.customer.internal.Customer
         *   o com.bank.customer.internal.CustomerRepository
         *
         * # Module account
         * > Dependencies:
         *   + customer (CustomerService, CustomerRegisteredEvent)
         *
         * This output is invaluable for understanding and documenting
         * the real module structure as it exists in code.
         */
        modules.forEach(System.out::println);
    }

    @Test
    void applicationModules_generateDocumentation() {
        /*
         * Spring Modulith can auto-generate architecture documentation.
         *
         * This generates:
         * - A PlantUML/C4 component diagram of all modules and their dependencies
         * - Per-module documentation files
         *
         * Output location: target/spring-modulith-docs/
         *
         * The diagram is generated FROM the actual code, not from a
         * manually maintained diagram. This means the documentation
         * is always up to date and accurate.
         */
        new Documenter(modules)
            .writeModulesAsPlantUml()
            .writeIndividualModulesAsPlantUml();
    }
}
