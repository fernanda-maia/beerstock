package one.digitalinnovation.beerstock.controller;

import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.exception.BeerAlreadyRegisteredException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Api("Manages the beer stock")
public interface BeerCotrollerDocs {

    @ApiOperation(value = "Create beer")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Beer created sucessfully"),
            @ApiResponse(code = 404, message = "Beer not found")
    })
    BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException;

    @ApiOperation(value = "Find beer by a given name")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Beer found successfully"),
            @ApiResponse(code = 404, message = "Beer not found")
    })
    BeerDTO findByName(@PathVariable String name) throws BeerNotFoundException;

    @ApiOperation(value = "List all beers registered in database")
    @ApiResponse(code = 200, message = "All beers listed successfully")
    List<BeerDTO> listBeers();

    @ApiOperation(value = "Delete beer by a given id")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Beer deleted successfully"),
            @ApiResponse(code = 404, message = "Beer not found")
    })
    void deleteById(@PathVariable Long id) throws BeerNotFoundException;
}
