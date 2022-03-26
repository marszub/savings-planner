package pl.edu.agh.kuce.planner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlannerApiApplication {

    public static void main(String[] args) {
        System.out.println("Test");
        SpringApplication.run(PlannerApiApplication.class, args);
    }
}
