package one.digitalinnovation.beerstock.service;

import one.digitalinnovation.beerstock.entity.Beer;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.mapper.BeerMapper;
import one.digitalinnovation.beerstock.builder.BeerDTOBuilder;
import one.digitalinnovation.beerstock.repository.BeerRepository;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.exception.BeerStockExceededException;
import one.digitalinnovation.beerstock.exception.BeerStockNegativeException;
import one.digitalinnovation.beerstock.exception.BeerAlreadyRegisteredException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

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

    @Test
    void whenDeleteIsCalledWithAValidIdABeerShouldBeDeleted()
            throws BeerNotFoundException {

        // GIVEN
        BeerDTO expectedDeletedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedDeletedBeer = beerMapper.toModel(expectedDeletedBeerDTO);

        // WHEN
        when(beerRepository.findById(expectedDeletedBeer.getId()))
                .thenReturn(Optional.of(expectedDeletedBeer));

        doNothing().when(beerRepository)
                .deleteById(expectedDeletedBeerDTO.getId());

        // THEN
        beerService.deleteById(expectedDeletedBeerDTO.getId());

        verify(beerRepository, times(1))
                .findById(expectedDeletedBeerDTO.getId());

        verify(beerRepository, times(1))
                .deleteById(expectedDeletedBeerDTO.getId());
    }

    @Test
    void whenDeleteIsCalledWithInvalidIdAnExceptionShouldBeThrown() {
        // GIVEN
        BeerDTO expectedDeletedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // WHEN
        when(beerRepository.findById(expectedDeletedBeerDTO.getId()))
                .thenReturn(Optional.empty());

        // THROW
        assertThrows(BeerNotFoundException.class,
                () -> beerService.deleteById(expectedDeletedBeerDTO.getId()));
    }

    @Test
    void whenIncrementIsCalledWithAValidIdThenIncrementBeerStock()
            throws BeerNotFoundException, BeerStockExceededException {

        // GIVEN
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

        int quantityToIncrement = 10;
        int expectedQuantity = expectedBeerDTO.getQuantity() + quantityToIncrement;

        // WHEN
        when(beerRepository.findById(expectedBeerDTO.getId()))
                .thenReturn(Optional.of(expectedBeer));

        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

        // THEN
        BeerDTO incrementedBeer = beerService.increment(expectedBeerDTO.getId(), quantityToIncrement);

        assertThat(expectedQuantity, equalTo(incrementedBeer.getQuantity()));
        assertThat(expectedQuantity, lessThan(incrementedBeer.getMax()));

    }

    @Test
    void whenIncrementIsGreaterThanMaxThenThrowsAnException() {

        // GIVEN
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

        int quantityToIncrement = 80;

        // WHEN
        when(beerRepository.findById(expectedBeerDTO.getId()))
                .thenReturn(Optional.of(expectedBeer));

        // THROW
        assertThrows(BeerStockExceededException.class,
                () -> beerService.increment(expectedBeerDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementAfterSumIsGreaterThanMaxThenThrowsAnException() {

        // GIVEN
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

        int quantityToIncrement = 41;

        // WHEN
        when(beerRepository.findById(expectedBeerDTO.getId()))
                .thenReturn(Optional.of(expectedBeer));

        // THROW
        assertThrows(BeerStockExceededException.class,
                () -> beerService.increment(expectedBeerDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowAnException() {

        // GIVEN
        int quantityToIncrement = 10;

        // WHEN
        when(beerRepository.findById(INVALID_BEER_ID))
                .thenReturn(Optional.empty());

        // THROW
        assertThrows(BeerNotFoundException.class,
                () -> beerService.increment(INVALID_BEER_ID, quantityToIncrement));
    }

    @Test
    void whenDecrementIsCalledWithAValidIdThenDecrementBeerStock()
            throws BeerNotFoundException, BeerStockNegativeException {

        // GIVEN
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

        int quantityToDecrement = 10;
        int expectedQuantity = expectedBeerDTO.getQuantity() - quantityToDecrement;

        // WHEN
        when(beerRepository.findById(expectedBeerDTO.getId()))
                .thenReturn(Optional.of(expectedBeer));

        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

        // THEN
        BeerDTO decrementedBeer = beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement);

        assertThat(expectedQuantity, equalTo(decrementedBeer.getQuantity()));
        assertThat(expectedQuantity, greaterThanOrEqualTo(0));
    }

    @Test
    void whenDecrementAfterSubIsLessThanZeroThenThrowsAnException() {

        // GIVEN
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

        int quantityToDecrement = 11;

        // WHEN
        when(beerRepository.findById(expectedBeerDTO.getId()))
                .thenReturn(Optional.of(expectedBeer));

        // THROW
        assertThrows(BeerStockNegativeException.class,
                () -> beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement));
    }

    @Test
    void whenDecrementIsCalledWithInvalidIdThenThrowAnException() {

        // GIVEN
        int quantityToDecrement = 10;

        // WHEN
        when(beerRepository.findById(INVALID_BEER_ID))
                .thenReturn(Optional.empty());

        // THROW
        assertThrows(BeerNotFoundException.class,
                () -> beerService.increment(INVALID_BEER_ID, quantityToDecrement));
    }

}
