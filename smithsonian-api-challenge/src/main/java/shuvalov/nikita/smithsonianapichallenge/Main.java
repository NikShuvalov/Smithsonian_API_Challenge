package shuvalov.nikita.smithsonianapichallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import shuvalov.nikita.smithsonianapichallenge.database.ShowDbHelper;


@SpringBootApplication
public class Main {


	public static void main(String[] args) {
        ShowDbHelper.instantiate();
		SpringApplication.run(Main.class, args);
	}
}
