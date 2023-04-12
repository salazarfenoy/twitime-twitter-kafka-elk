package com.iesalandalus.proyecto.controllers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.iesalandalus.proyecto.model.Tema;
import com.iesalandalus.proyecto.model.Temas;
import com.iesalandalus.proyecto.model.Tweets;
import com.iesalandalus.proyecto.services.TwitterProducer;
import com.twitter.clientlib.ApiException;

@Controller
public class TwitterController {

	@Autowired
	private Tweets tweets;

	@Autowired
	private Temas temas;

	@Autowired
	public TwitterProducer client;
	
	private boolean esPrimeraEjecucion = false;

	Runnable command = new Runnable() {
		@Override
		public void run() {
			try {
				client.comenzar();
			} catch (ApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	ExecutorService executor = Executors.newFixedThreadPool(5);
	int contador = 0;

	@GetMapping("/")
	public String index(Model model) {

		model.addAttribute("tweetsList", tweets.getAll());
		model.addAttribute("temasList", temas.getAll());
		model.addAttribute("tema", new Tema());

		return "index";

	}

	@PostMapping(value = "/topics")
	public String saveTopic(@ModelAttribute Tema tema) throws ApiException {

		if (tema.getNombre().contains(",")) {
			converter(tema.getNombre());

		} else {
			temas.temas.add(tema);

		}
		
		
		if(!esPrimeraEjecucion) {
			esPrimeraEjecucion=true;
			
			executor.execute(command);
			
		} else {
			
			client.addAndDelete();
			
		}


		return "redirect:/";

	}
	
	@GetMapping(value = "/deleteList")
	public String aa() {

		tweets.delete();

		return "redirect:/";

	}

	@RequestMapping(value = "/deletetopic", method = RequestMethod.GET)
	public String deleteTopic(@RequestParam(name = "topic") String topic) throws ApiException {
		Tema t = new Tema(topic);

		temas.temas.remove(t);
		
		client.addAndDelete();
		return "redirect:/";

	}

	public void converter(String palabras) {

		String[] palabra = palabras.split(",");

		for (int i = 0; i < palabra.length; i++) {
			Tema t = new Tema(palabra[i].trim());
			temas.temas.add(t);
		}

	}
}
