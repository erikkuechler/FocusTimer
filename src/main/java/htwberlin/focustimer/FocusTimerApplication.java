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
		if (repository.count() == 0) {
			// Foreground
			repository.save(new Product("Baum", 3, "Foreground", "Baum_Default"));
			repository.save(new Product("Apfelbaum", 5, "Foreground", "Baum_Apple"));
			repository.save(new Product("Weihnachtsbaum", 10, "Foreground", "Baum_Christmas"));
			// Background
			repository.save(new Product("Blau", 3, "Background", "bg-blue"));
			repository.save(new Product("Gold", 8, "Background", "bg-gold"));
			repository.save(new Product("Premium Grau", 15, "Background", "bg-gray"));
		}
		};
	}

}
