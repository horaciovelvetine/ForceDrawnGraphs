package edu.ForceDrawnGraphs;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ForceDrawnGraphs implements CommandLineRunner {

	public static void main(String[] args) {
		ApplicationContext contexto = new SpringApplicationBuilder(ForceDrawnGraphs.class)
				.web(WebApplicationType.NONE).headless(false).bannerMode(Banner.Mode.OFF).run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			frame.setVisible(true);
		});
	}
}
