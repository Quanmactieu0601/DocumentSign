package vn.easyca.signserver.webapp;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {

        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("vn.easyca.signserver.webapp");

        noClasses()
            .that()
                .resideInAnyPackage("vn.easyca.signserver.webapp.service..")
            .or()
                .resideInAnyPackage("vn.easyca.signserver.webapp.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..vn.easyca.signserver.webapp.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses);
    }
}
