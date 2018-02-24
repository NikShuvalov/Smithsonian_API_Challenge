package shuvalov.nikita.smithsonianapichallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import shuvalov.nikita.smithsonianapichallenge.database.ShowDbHelper;


@SpringBootApplication
public class SmithsonianApiChallengeApplication {


	public static void main(String[] args) {
        ShowDbHelper.instantiate();
		SpringApplication.run(SmithsonianApiChallengeApplication.class, args);
	}

}
