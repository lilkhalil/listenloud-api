package com.lilkhalil.listenloud;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import com.lilkhalil.listenloud.model.Tag;
import com.lilkhalil.listenloud.model.TagType;
import com.lilkhalil.listenloud.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@Component
@RequiredArgsConstructor
public class ListenloudApplication implements CommandLineRunner {

	private final TagRepository tagRepository;

	public static void main(String[] args) {
		SpringApplication.run(ListenloudApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		for (TagType tagType : TagType.values())
			tagRepository.save(Tag.builder().name(tagType).build());
	}

}
