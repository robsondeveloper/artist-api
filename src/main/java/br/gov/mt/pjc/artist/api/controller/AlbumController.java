package br.gov.mt.pjc.artist.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.gov.mt.pjc.artist.api.contract.request.AlbumRequest;
import br.gov.mt.pjc.artist.api.contract.response.AlbumResponse;
import br.gov.mt.pjc.artist.service.AlbumService;
import br.gov.mt.pjc.artist.service.MinioService;

@RestController
@RequestMapping("/v1/albums")
public class AlbumController {

	private final AlbumService service;
	private final MinioService minioService;

	public AlbumController(AlbumService service, MinioService minioService) {
		this.service = service;
		this.minioService = minioService;
	}

	@GetMapping
	public List<AlbumResponse> findAll() {
		return service.findAll();
	}

	@GetMapping(path = "/page")
	public Page<AlbumResponse> page(@PageableDefault(size = 10) Pageable pageable) {
		return service.findAll(pageable);
	}

	@GetMapping(path = "/{id}")
	public AlbumResponse findById(@PathVariable Long id) {
		return service.findById(id);
	}

	@GetMapping(path = "/search")
	public List<AlbumResponse> findByArtistName(@RequestParam(name = "artistName") String artistName) {
		return service.findByArtistName(artistName);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public AlbumResponse create(@RequestBody @Valid AlbumRequest request) {
		return service.create(request);
	}

	@PutMapping(path = "/{id}")
	public AlbumResponse update(@PathVariable Long id, @RequestBody @Valid AlbumRequest request) {
		return service.update(id, request);
	}

	@DeleteMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}

	@PutMapping(path = "/{id}/covers")
	public void uploadCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
		service.findById(id);
		String objectName = minioService.upload(file);
		service.addCover(id, objectName);
	}

	@DeleteMapping(path = "/covers/{objectName}")
	public void deleteCover(@PathVariable String objectName) {
		minioService.remove(objectName);
		service.removeCover(objectName);
	}

}
