package one.digitalinnovation.beerstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BeerNotFoundException extends Exception {

    public BeerNotFoundException(String beerName) {
        super(beerName + " not found!");
    }

    public BeerNotFoundException() {
        super("ID not registered!");
    }
}
