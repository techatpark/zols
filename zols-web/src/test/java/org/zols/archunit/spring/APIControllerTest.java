package org.zols.archunit.spring;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction;
import com.tngtech.archunit.lang.syntax.elements.GivenMethodsConjunction;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

/**
 * The type Api controller test.
 */
public class APIControllerTest {

    private static final GivenClassesConjunction CONTROLLER_CLASSES = classes()
            .that()
            .areAnnotatedWith(RestController.class);

    private static final GivenMethodsConjunction CONTROLLER_PUBLIC_METHODS
            = methods().that().areDeclaredInClassesThat()
            .areAnnotatedWith(RestController.class).and().arePublic();

    @Test
    public void controller_immutable_stateless() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_ARCHIVES)
                .importPackages("org.zols");

        ArchRule rule = CONTROLLER_CLASSES
                .should()
                .bePackagePrivate()
                .andShould().haveOnlyFinalFields()
                .andShould().accessClassesThat()
                .resideOutsideOfPackage("javax.validation")
                .andShould().onlyDependOnClassesThat()
                .resideInAnyPackage(
                        "org.zols.core.controller.api"
                        , "javax.servlet.http"
                        , "javax.servlet"
                        , "java.io"
                        , "java.net"
                        , "org.zols.starter.security.security"
                        , "org.springframework.security.authentication"
                        , "org.springframework.beans.factory.annotation"
                        , "java.util"
                        , "..service.."
                        , "..model.."
                        , "..payload.."
                        , "java.lang"
                        , "org.springframework.http"
                        , "com.fasterxml.jackson.core"
                        , "org.springframework.web.bind.annotation"
                        , "io.swagger.v3.oas.annotations"
                        , "io.swagger.v3.oas.annotations.tags"
                        , "io.swagger.v3.oas.annotations.parameters"
                        , "org.springframework.web.bind"
                        , "io.swagger.v3.oas.annotations.responses"
                        , "io.swagger.v3.oas.annotations.security"
                        , "java.security")
                .because("Controllers should only be delegates");

        rule.check(importedClasses);

        rule = CONTROLLER_PUBLIC_METHODS
                .should()
                .haveRawReturnType(ResponseEntity.class)
                .andShould().notBeAnnotatedWith(Valid.class)
                .because("we don't want to expose domain models directly");

        rule.check(importedClasses);


    }




}