@org.springframework.modulith.ApplicationModule(
        displayName = "Payments",
        allowedDependencies = {"shared", "identity", "accounts", "ledger", "fraud"})
package com.novabank.payments;
