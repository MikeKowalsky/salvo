package salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepo,
									  GameRepository gameRepo,
									  GamePlayerRepository gamePlayerRepo,
									  ShipRepository shipRepo,
									  SalvoRepository salvoRepo,
									  ScoreRepository scoreRepo) {
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

			Ship s01 = new Ship("Destroyer");
			s01.setLocations(new ArrayList<String>(Arrays.asList("H2", "H3", "H4")));
			gp1.addShip(s01);
			Ship s02 = new Ship("Submarine");
			s02.setLocations(new ArrayList<String>(Arrays.asList("E1", "F1", "G1")));
			gp1.addShip(s02);
			Ship s03 = new Ship("Patrol Boat");
			s03.setLocations(new ArrayList<String>(Arrays.asList("B4", "B5")));
			gp1.addShip(s03);
			Ship s04 = new Ship("Destroyer");
			s04.setLocations(new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
			gp2.addShip(s04);
			Ship s05 = new Ship("Patrol Boat");
			s05.setLocations(new ArrayList<String>(Arrays.asList("F1", "F2")));
			gp2.addShip(s05);
			Ship s06 = new Ship("Destroyer");
			s06.setLocations(new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
			gp3.addShip(s06);
			Ship s07 = new Ship("Patrol Boat");
			s07.setLocations(new ArrayList<String>(Arrays.asList("C6", "C7")));
			gp3.addShip(s07);
			Ship s08 = new Ship("Submarine");
			s08.setLocations(new ArrayList<String>(Arrays.asList("A2", "A4", "A5")));
			gp4.addShip(s08);
			Ship s09 = new Ship("Patrol Boat");
			s09.setLocations(new ArrayList<String>(Arrays.asList("G6", "H6")));
			gp4.addShip(s09);

			Ship s10 = new Ship("Destroyer");
			s10.setLocations(new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
			gp5.addShip(s10);
			Ship s11 = new Ship("Patrol Boat");
			s11.setLocations(new ArrayList<String>(Arrays.asList("C6", "C7")));
			gp5.addShip(s11);
			Ship s12 = new Ship("Submarine");
			s12.setLocations(new ArrayList<String>(Arrays.asList("A2", "A3", "A4")));
			gp6.addShip(s12);
			Ship s13 = new Ship("Patrol Boat");
			s13.setLocations(new ArrayList<String>(Arrays.asList("G6", "H6")));
			gp6.addShip(s13);

			Ship s14 = new Ship("Destroyer");
			s14.setLocations(new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
			gp7.addShip(s14);
			Ship s15 = new Ship("Patrol Boat");
			s15.setLocations(new ArrayList<String>(Arrays.asList("C6", "C7")));
			gp7.addShip(s15);
			Ship s16 = new Ship("Submarine");
			s16.setLocations(new ArrayList<String>(Arrays.asList("A2", "A3", "A4")));
			gp8.addShip(s16);
			Ship s17 = new Ship("Patrol Boat");
			s17.setLocations(new ArrayList<String>(Arrays.asList("G6", "H6")));
			gp8.addShip(s17);

			Ship s18 = new Ship("Destroyer");
			s18.setLocations(new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
			gp7.addShip(s18);
			Ship s19 = new Ship("Patrol Boat");
			s19.setLocations(new ArrayList<String>(Arrays.asList("C6", "C7")));
			gp7.addShip(s19);
			Ship s20 = new Ship("Submarine");
			s20.setLocations(new ArrayList<String>(Arrays.asList("A2", "A3", "A4")));
			gp8.addShip(s20);
			Ship s21 = new Ship("Patrol Boat");
			s21.setLocations(new ArrayList<String>(Arrays.asList("G6", "H6")));
			gp8.addShip(s21);

			Ship s22 = new Ship("Destroyer");
			s22.setLocations(new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
			gp9.addShip(s22);
			Ship s23 = new Ship("Patrol Boat");
			s23.setLocations(new ArrayList<String>(Arrays.asList("C6", "C7")));
			gp9.addShip(s23);
			Ship s24 = new Ship("Submarine");
			s24.setLocations(new ArrayList<String>(Arrays.asList("A2", "A3", "A4")));
			gp10.addShip(s24);
			Ship s25 = new Ship("Patrol Boat");
			s25.setLocations(new ArrayList<String>(Arrays.asList("G6", "H6")));
			gp10.addShip(s25);

			Ship s26 = new Ship("Destroyer");
			s26.setLocations(new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
			gp11.addShip(s26);
			Ship s27 = new Ship("Patrol Boat");
			s27.setLocations(new ArrayList<String>(Arrays.asList("C6", "C7")));
			gp11.addShip(s27);

			Ship s28 = new Ship("Destroyer");
			s28.setLocations(new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
			gp13.addShip(s28);
			Ship s29 = new Ship("Patrol Boat");
			s29.setLocations(new ArrayList<String>(Arrays.asList("C6", "C7")));
			gp13.addShip(s29);
			Ship s30 = new Ship("Submarine");
			s30.setLocations(new ArrayList<String>(Arrays.asList("A2", "A3", "A4")));
			gp14.addShip(s30);
			Ship s31 = new Ship("Patrol Boat");
			s31.setLocations(new ArrayList<String>(Arrays.asList("G6", "H6")));
			gp14.addShip(s31);

			shipRepo.save(s01);
			shipRepo.save(s02);
			shipRepo.save(s03);
			shipRepo.save(s04);
			shipRepo.save(s05);
			shipRepo.save(s06);
			shipRepo.save(s07);
			shipRepo.save(s08);
			shipRepo.save(s09);
			shipRepo.save(s10);
			shipRepo.save(s11);
			shipRepo.save(s12);
			shipRepo.save(s13);
			shipRepo.save(s14);
			shipRepo.save(s15);
			shipRepo.save(s16);
			shipRepo.save(s17);
			shipRepo.save(s18);
			shipRepo.save(s19);
			shipRepo.save(s20);
			shipRepo.save(s21);
			shipRepo.save(s22);
			shipRepo.save(s23);
			shipRepo.save(s24);
			shipRepo.save(s25);
			shipRepo.save(s26);
			shipRepo.save(s27);
			shipRepo.save(s28);
			shipRepo.save(s29);
			shipRepo.save(s30);
			shipRepo.save(s31);

			Salvo slv01 = new Salvo(1);
			slv01.setLocations(new ArrayList<String>(Arrays.asList("B5", "C5", "F1")));
			gp1.addSalvo(slv01);
			Salvo slv02 = new Salvo(1);
			slv02.setLocations(new ArrayList<String>(Arrays.asList("B4", "B5", "B6")));
			gp2.addSalvo(slv02);
			Salvo slv03 = new Salvo(2);
			slv03.setLocations(new ArrayList<String>(Arrays.asList("F2", "D5")));
			gp1.addSalvo(slv03);
			Salvo slv04 = new Salvo(2);
			slv04.setLocations(new ArrayList<String>(Arrays.asList("E1", "H3", "A2")));
			gp2.addSalvo(slv04);

			Salvo slv05 = new Salvo(1);
			slv05.setLocations(new ArrayList<String>(Arrays.asList("A2", "A4", "G6")));
			gp4.addSalvo(slv05);
			Salvo slv06 = new Salvo(1);
			slv06.setLocations(new ArrayList<String>(Arrays.asList("B5", "D5", "C7")));
			gp3.addSalvo(slv06);
			Salvo slv07 = new Salvo(2);
			slv07.setLocations(new ArrayList<String>(Arrays.asList("A3", "H6")));
			gp4.addSalvo(slv07);
			Salvo slv08 = new Salvo(2);
			slv08.setLocations(new ArrayList<String>(Arrays.asList("C5", "C6")));
			gp3.addSalvo(slv08);

			Salvo slv09 = new Salvo(1);
			slv09.setLocations(new ArrayList<String>(Arrays.asList("G6", "H6", "A4")));
			gp5.addSalvo(slv09);
			Salvo slv10 = new Salvo(1);
			slv10.setLocations(new ArrayList<String>(Arrays.asList("H1", "H2", "H3")));
			gp6.addSalvo(slv10);
			Salvo slv11 = new Salvo(2);
			slv11.setLocations(new ArrayList<String>(Arrays.asList("A2", "A3", "D8")));
			gp5.addSalvo(slv11);
			Salvo slv12 = new Salvo(2);
			slv12.setLocations(new ArrayList<String>(Arrays.asList("E1", "F2", "G3")));
			gp6.addSalvo(slv12);

			Salvo slv13 = new Salvo(1);
			slv13.setLocations(new ArrayList<String>(Arrays.asList("A3", "A4", "F4")));
			gp8.addSalvo(slv13);
			Salvo slv14 = new Salvo(1);
			slv14.setLocations(new ArrayList<String>(Arrays.asList("B5", "C6", "H1")));
			gp7.addSalvo(slv14);
			Salvo slv15 = new Salvo(2);
			slv15.setLocations(new ArrayList<String>(Arrays.asList("A2", "G6", "H6")));
			gp8.addSalvo(slv15);
			Salvo slv16 = new Salvo(2);
			slv16.setLocations(new ArrayList<String>(Arrays.asList("C5", "C7", "D5")));
			gp7.addSalvo(slv16);

			Salvo slv17 = new Salvo(1);
			slv17.setLocations(new ArrayList<String>(Arrays.asList("A1", "A2", "A3")));
			gp9.addSalvo(slv17);
			Salvo slv18 = new Salvo(1);
			slv18.setLocations(new ArrayList<String>(Arrays.asList("B5", "B6", "C7")));
			gp10.addSalvo(slv18);
			Salvo slv19 = new Salvo(2);
			slv19.setLocations(new ArrayList<String>(Arrays.asList("G6", "G7", "G8")));
			gp9.addSalvo(slv19);
			Salvo slv20 = new Salvo(2);
			slv20.setLocations(new ArrayList<String>(Arrays.asList("C6", "D6", "E6")));
			gp10.addSalvo(slv20);
			Salvo slv21 = new Salvo(3);
			slv21.setLocations(new ArrayList<String>(Arrays.asList("H1", "H8")));
			gp10.addSalvo(slv21);

			salvoRepo.save(slv01);
			salvoRepo.save(slv02);
			salvoRepo.save(slv03);
			salvoRepo.save(slv04);
			salvoRepo.save(slv05);
			salvoRepo.save(slv06);
			salvoRepo.save(slv07);
			salvoRepo.save(slv08);
			salvoRepo.save(slv09);
			salvoRepo.save(slv10);
			salvoRepo.save(slv11);
			salvoRepo.save(slv12);
			salvoRepo.save(slv13);
			salvoRepo.save(slv14);
			salvoRepo.save(slv15);
			salvoRepo.save(slv16);
			salvoRepo.save(slv17);
			salvoRepo.save(slv18);
			salvoRepo.save(slv19);
			salvoRepo.save(slv20);
			salvoRepo.save(slv21);

			Score sc01 = new Score(p1, g1, 1.0);
			Score sc02 = new Score(p2, g1, 0.0);
			Score sc03 = new Score(p1, g2, 0.5);
			Score sc04 = new Score(p2, g2, 0.5);
			Score sc05 = new Score(p2, g3, 1.0);
			Score sc06 = new Score(p4, g3, 0.0);
			Score sc07 = new Score(p2, g4, 0.5);
			Score sc08 = new Score(p1, g4, 0.5);

			scoreRepo.save(sc01);
			scoreRepo.save(sc02);
			scoreRepo.save(sc03);
			scoreRepo.save(sc04);
			scoreRepo.save(sc05);
			scoreRepo.save(sc06);
			scoreRepo.save(sc07);
			scoreRepo.save(sc08);

		};
	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepo;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(inputName-> {
			Player player = playerRepo.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
//					.antMatchers("/admin/**").hasAuthority("ADMIN")
//					.antMatchers("/**").hasAuthority("USER")
					.antMatchers("/**").permitAll()
					.and()
				.formLogin()
					.usernameParameter("name")
					.passwordParameter("pwd")
					.loginPage("/api/login")
					.and()
				.logout()
					.logoutUrl("/api/logout");


		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}



}