package com.example.demo;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@RestController
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	private static final String CLIENT_ID = "572182";
	private static final String CLIENT_SECRET = "66d55dc4fd706c8ba2864c72495b44b7";
	private static final String FLAG = "$CTF[" + UUID.randomUUID() +"]";
	public static User user= null;

	@GetMapping("/")
	public Mono<ResponseEntity> home() {
		if (user != null) {
			StringBuilder body = new StringBuilder("Hello world " + user.name + "!");
			if (user.email.equals("qcastel+deezeradmin@wearehackerone.com")) {
				body.append("You're the admin. Here is the secret flag: " + FLAG);
			} else {
				body.append("You're not an admin");
			}
			return Mono.just(new ResponseEntity(body.toString(), HttpStatus.OK));
		}
		return Mono.just(new HttpHeaders())
				.doOnNext(header -> header.add("Location", "https://connect.deezer.com/oauth/auth.php?app_id=" + CLIENT_ID + "&perms=basic_access,email&redirect_uri=http://localhost:8081/callback"))
				.map(header -> new ResponseEntity<>(null, header, HttpStatus.TEMPORARY_REDIRECT));
	}

	@GetMapping("/callback")
	public Mono<ResponseEntity> callback(@RequestParam(name = "code") String code) {

		WebClient client = WebClient.create("https://connect.deezer.com");
		return client.method(HttpMethod.GET)
				.uri("oauth/access_token.php?app_id=" + CLIENT_ID+ "&secret=" + CLIENT_SECRET + "&code=" + code)
				.retrieve()
				.bodyToMono(String.class)
				.flatMap(at -> {
					Map<String, String> response = Arrays.stream(at.split("&")).map(k -> k.split("=")).collect(Collectors.toMap(s -> s[0], s -> s[1]));
					return WebClient.create("https://api.deezer.com")
							.method(HttpMethod.GET)
							.uri("/user/me?access_token=" + response.get("access_token"))
							.retrieve()
							.bodyToMono(User.class)
							.flatMap(u -> {
								user = u;
								HttpHeaders httpHeaders = new HttpHeaders();
								httpHeaders.add("Location", "/");
								return Mono.just(new ResponseEntity<>(null, httpHeaders, HttpStatus.TEMPORARY_REDIRECT));
							});
				});
	}

	@GetMapping("/logout")
	public Mono<ResponseEntity> logout() {
		user = null;
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Location", "/");
		return Mono.just(new ResponseEntity<>(null, httpHeaders, HttpStatus.TEMPORARY_REDIRECT));
	}

}
