package io.github.innobridge.llama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
    io.github.innobridge.llama.configuration.ApplicationSpecificSpringComponentScanMarker.class,
    io.github.innobridge.llama.controller.ApplicationSpecificSpringComponentScanMarker.class,
})
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
