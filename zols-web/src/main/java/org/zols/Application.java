/**
 * package.info.
 */
package org.zols;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import static org.springframework.boot.SpringApplication.run;

/**
 * The type Application.
 */
@SpringBootApplication
public final class Application {

    /**
     * Application default constructor.
     */
    private Application() {
    }

    /**
     * Main method of this application.
     *
     * @param args the args
     */
    public static void main(final String[] args) {
        ApplicationContext ctx = run(Application.class, args);
    }
}
