package com.lilkhalil.listenloud.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class IndexController {

    @GetMapping
	public RepresentationModel<?> index() {
		RepresentationModel<?> index = new RepresentationModel<>();
		index.add(linkTo(MusicController.class).withRel("Сервис музыки"));
		index.add(linkTo(AuthenticationController.class).withRel("Сервис аутентификации"));
		return index;
	}
    
}
