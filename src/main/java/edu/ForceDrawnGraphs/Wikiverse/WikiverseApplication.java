package edu.ForceDrawnGraphs.Wikiverse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import edu.ForceDrawnGraphs.Wikiverse.db.LocalDatabase;

@SpringBootApplication
public class WikiverseApplication {
	private final LocalDatabase db = new LocalDatabase();

	public static void main(String[] args) {
		// put something here to config run
		SpringApplication.run(WikiverseApplication.class, args);
	}

}
