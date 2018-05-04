package salvo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepo,
									  GameRepository gameRepo,
									  GamePlayerRepository gamePlayerRepo) {
		return (args) -> {

			// save a couple of players
			Player p1 = new Player("Jack Bauer", "j.bauer@ctu.gov", "24");
			Player p2 = new Player("Chloe O'Brian","c.obrian@ctu.gov", "42");
			Player p3 = new Player("Kim Bauer","kim_bauer@gmail.com", "kb");
			Player p4 = new Player("Tony Almeida","t.almeida@ctu.gov", "mole");

			playerRepo.save(p1);
			playerRepo.save(p2);
			playerRepo.save(p3);
			playerRepo.save(p4);

			// save a couple of games
			Game g1 = new Game();
			Game g2 = new Game(3600);
			Game g3 = new Game(7200);
			Game g4 = new Game(10800);
			Game g5 = new Game(14400);
			Game g6 = new Game(18000);
			Game g7 = new Game(21600);
			Game g8 = new Game(25200);

			gameRepo.save(g1);
			gameRepo.save(g2);
			gameRepo.save(g3);
			gameRepo.save(g4);
			gameRepo.save(g5);
			gameRepo.save(g6);
			gameRepo.save(g7);
			gameRepo.save(g8);

			// save a couple of gameplayers
			GamePlayer gp1 = new GamePlayer(p1, g1);
			GamePlayer gp2 = new GamePlayer(p2, g1);
			GamePlayer gp3 = new GamePlayer(p1, g2);
			GamePlayer gp4 = new GamePlayer(p2, g2);
			GamePlayer gp5 = new GamePlayer(p2, g3);
			GamePlayer gp6 = new GamePlayer(p4, g3);
			GamePlayer gp7 = new GamePlayer(p2, g4);
			GamePlayer gp8 = new GamePlayer(p1, g4);
			GamePlayer gp9 = new GamePlayer(p4, g5);
			GamePlayer gp10 = new GamePlayer(p1, g5);
			GamePlayer gp11 = new GamePlayer(p3, g6);
			GamePlayer gp12 = new GamePlayer(p4, g7);
			GamePlayer gp13 = new GamePlayer(p3, g8);
			GamePlayer gp14 = new GamePlayer(p4, g8);

			gamePlayerRepo.save(gp1);
			gamePlayerRepo.save(gp2);
			gamePlayerRepo.save(gp3);
			gamePlayerRepo.save(gp4);
			gamePlayerRepo.save(gp5);
			gamePlayerRepo.save(gp6);
			gamePlayerRepo.save(gp7);
			gamePlayerRepo.save(gp8);
			gamePlayerRepo.save(gp9);
			gamePlayerRepo.save(gp10);
			gamePlayerRepo.save(gp11);
			gamePlayerRepo.save(gp12);
			gamePlayerRepo.save(gp13);
			gamePlayerRepo.save(gp14);

		};
	}
}