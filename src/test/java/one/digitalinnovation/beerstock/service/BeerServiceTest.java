package one.digitalinnovation.beerstock.service;

import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.entity.Beer;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.mapper.BeerMapper;
import one.digitalinnovation.beerstock.builder.BeerDTOBuilder;
import one.digitalinnovation.beerstock.repository.BeerRepository;
import one.digitalinnovation.beerstock.exception.BeerAlreadyRegisteredException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

    private static final long INVALID_BEER_ID = 1L;

    @Mock
    private BeerRepository beerRepository;

    private BeerMapper beerMapper = BeerMapper.INSTANCE;

    @InjectMocks
    private BeerService beerService;

    @Test
    void whenBeerInformedThenItShouldBeCreated()
            throws BeerAlreadyRegisteredException {

        // GIVEN
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedSavedBeer = beerMapper.toModel(beerDTO);

        // WHEN
        when(beerRepository.findByName(beerDTO.getName()))
                .thenReturn(Optional.empty());

        when(beerRepository.save(expectedSavedBeer))
                .thenReturn(expectedSavedBeer);

        // THEN
        BeerDTO createdBeerDTO = beerService.createBeer(beerDTO);

        assertThat(createdBeerDTO.getId(), is(equalTo(beerDTO.getId())));
        assertThat(createdBeerDTO.getName(), is(equalTo(beerDTO.getName())));
        assertThat(createdBeerDTO.getQuantity(), is(equalTo(beerDTO.getQuantity())));

    }

    @Test
    void whenAlreadyRegisteredBeerInformedThenExceptionShouldBeThrown() {

        // GIVEN
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer duplicatedBeer = beerMapper.toModel(beerDTO);

        // WHEN
        when(beerRepository.findByName(beerDTO.getName()))
                .thenReturn(Optional.of(duplicatedBeer));

        // THROW
        assertThrows(BeerAlreadyRegisteredException.class,
                () -> beerService.createBeer(beerDTO));

    }

    @Test
    void whenAValidBeerNameIsGivenThenReturnABeer()
            throws BeerNotFoundException {

        // GIVEN
        BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);

        // WHEN
        when(beerRepository.findByName(expectedFoundBeer.getName()))
                .thenReturn(Optional.of(expectedFoundBeer));

        // THEN
        BeerDTO foundBeerDTO = beerService.findByName(expectedFoundBeerDTO.getName());

        assertThat(foundBeerDTO, is(equalTo(expectedFoundBeerDTO)));
    }

    @Test
    void whenANotRegisteredBeerNameIsGivenThenThrowAnException() {

        // GIVEN
        BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // WHEN
        when(beerRepository.findByName(expectedFoundBeerDTO.getName()))
                .thenReturn(Optional.empty());

        // THROW
        assertThrows(BeerNotFoundException.class,
                () -> beerService.findByName(expectedFoundBeerDTO.getName()));

    }

    @Test
    void whenListBeersIsCalledReturnAListOfBeers() {

        // GIVEN
        BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);

        // WHEN
        when(beerRepository.findAll())
                .thenReturn(Collections.singletonList(expectedFoundBeer));

        // THEN
        List<BeerDTO> listBeersDTO = beerService.listAll();

        assertThat(listBeersDTO, is(not(empty())));
        assertThat(listBeersDTO.get(0), is(equalTo(expectedFoundBeerDTO)));

    }

    @Test
    void whenListBeersIsCalledReturnAnEmptyList() {

        // WHEN
        when(beerRepository.findAll())
                .thenReturn(Collections.EMPTY_LIST);

        // THEN
        List<BeerDTO> listBeersDTO = beerService.listAll();

        assertThat(listBeersDTO, is(empty()));
    }
}
