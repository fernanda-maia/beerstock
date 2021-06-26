package one.digitalinnovation.beerstock.controller;

import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.dto.QuatityDTO;
import one.digitalinnovation.beerstock.service.BeerService;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.exception.BeerStockNegativeException;
import one.digitalinnovation.beerstock.exception.BeerStockExceededException;
import one.digitalinnovation.beerstock.exception.BeerAlreadyRegisteredException;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "api/v1/beers")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerController implements BeerCotrollerDocs {

    private final BeerService beerService;

    @GetMapping
    public List<BeerDTO> listBeers() {
        return beerService.listAll();
    }

    @GetMapping("/{name}")
    public BeerDTO findByName(@PathVariable String name)
            throws BeerNotFoundException {

        return beerService.findByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeerDTO createBeer(@RequestBody @Valid BeerDTO beerDTO)
            throws BeerAlreadyRegisteredException {

        return beerService.createBeer(beerDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id)
            throws BeerNotFoundException {

        beerService.deleteById(id);
    }

    @PatchMapping("/{id}/increment")
    public BeerDTO increment(@PathVariable Long id,
                             @RequestBody @Valid QuatityDTO quatityDTO)
            throws BeerNotFoundException, BeerStockExceededException {

        return beerService.increment(id, quatityDTO.getQuantity());
    }

    @PatchMapping("/{id}/decrement")
    public BeerDTO decrement(@PathVariable Long id,
                             @RequestBody @Valid QuatityDTO quatityDTO)
            throws BeerNotFoundException, BeerStockNegativeException {

        return beerService.decrement(id, quatityDTO.getQuantity());
    }
}
