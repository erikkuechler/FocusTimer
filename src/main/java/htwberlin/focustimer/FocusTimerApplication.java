package htwberlin.focustimer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import htwberlin.focustimer.entity.Product;
import htwberlin.focustimer.repository.ProductRepository;

@SpringBootApplication
public class FocusTimerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FocusTimerApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(ProductRepository repository) {
		return (args) -> {
		// save a few products
		repository.save(new Product("Default", 1, "Foreground", "Baum_Default.png"));
		repository.save(new Product("Weihnachtsbaum", 10, "Foreground", "Baum_Christmas.png"));
		};
	}

}
