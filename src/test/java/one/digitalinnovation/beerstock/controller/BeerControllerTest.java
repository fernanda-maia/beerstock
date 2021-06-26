package one.digitalinnovation.beerstock.controller;

import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.dto.QuatityDTO;
import one.digitalinnovation.beerstock.service.BeerService;
import one.digitalinnovation.beerstock.builder.BeerDTOBuilder;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.exception.BeerStockNegativeException;
import one.digitalinnovation.beerstock.exception.BeerStockExceededException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.Mock;
import org.mockito.InjectMocks;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static one.digitalinnovation.beerstock.utils.JSONCovertUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BeerControllerTest {

    private static final String BEER_API_URL_PATH = "/api/v1/beers";
    private static final long VALID_BEER_ID = 1L;
    private static final long INVALID_BEER_ID = 2L;
    private static final String BEER_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String BEER_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private BeerService beerService;

    @InjectMocks
    private BeerController beerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(beerController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) ->new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenABeerIsCreated()
            throws Exception {

        // GIVEN
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // WHEN
        when(beerService.createBeer(beerDTO)).thenReturn(beerDTO);

        // THEN
        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJSONString(beerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned()
            throws Exception {

        // GIVEN
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setBrand(null);

        // THEN
        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJSONString(beerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOKStatusIsReturned()
            throws Exception {

        // GIVEN
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // WHEN
        when(beerService.findByName(beerDTO.getName())).thenReturn(beerDTO);

        // THEN
        mockMvc.perform(get(BEER_API_URL_PATH + "/" + beerDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
    }

    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned()
            throws Exception {

        // GIVEN
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // WHEN
        when(beerService.findByName(beerDTO.getName()))
                .thenThrow(BeerNotFoundException.class);

        // THEN
        mockMvc.perform(get(BEER_API_URL_PATH + "/" + beerDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListBeersIsCalledThenOKStatusIsReturned()
            throws Exception {

        // GIVEN
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // WHEN
        when(beerService.listAll())
                .thenReturn(Collections.singletonList(beerDTO));

        // THEN
        mockMvc.perform(get(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(beerDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$[0].type", is(beerDTO.getType().toString())));
    }

    @Test
    void whenGETListWithoutBeersIsCalledThenOKStatusIsReturned()
            throws Exception {

        // WHEN
        when(beerService.listAll())
                .thenReturn(Collections.EMPTY_LIST);

        // THEN
        mockMvc.perform(get(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledWitValidIdThenNoContentStatusIsReturned()
            throws Exception {

        // GIVEN
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // WHEN
        doNothing().when(beerService).deleteById(beerDTO.getId());

        // THEN
        mockMvc.perform(delete(BEER_API_URL_PATH + "/" + beerDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEIsCalledWitInValidIdThenNotFoundStatusIsReturned()
            throws Exception {

        // WHEN
        doThrow(BeerNotFoundException.class)
                .when(beerService).deleteById(INVALID_BEER_ID);

        // THEN
        mockMvc.perform(delete(BEER_API_URL_PATH + "/" + INVALID_BEER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToIncrementThenOKStatusIsReturned()
            throws Exception {

        // GIVEN
        QuatityDTO quatityDTO = QuatityDTO.builder()
                .quantity(10)
                .build();

        BeerDTO incrementedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        incrementedBeerDTO.setQuantity(incrementedBeerDTO.getQuantity() + quatityDTO.getQuantity());

        // WHEN
        when(beerService.increment(incrementedBeerDTO.getId(), quatityDTO.getQuantity()))
                .thenReturn(incrementedBeerDTO);

        // THEN
        mockMvc.perform(
                patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJSONString(quatityDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(incrementedBeerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(incrementedBeerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(incrementedBeerDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(incrementedBeerDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledIncrementGreaterThanMaxThenABadRequestStatusIsReturned()
            throws Exception {

        // GIVEN
        QuatityDTO quatityDTO = QuatityDTO.builder()
                .quantity(41)
                .build();

        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        expectedBeerDTO.setQuantity(expectedBeerDTO.getQuantity() + quatityDTO.getQuantity());

        // WHEN
        when(beerService.increment(VALID_BEER_ID, quatityDTO.getQuantity()))
                .thenThrow(BeerStockExceededException.class);

        // THROW
        mockMvc.perform(
                patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJSONString(quatityDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHIsCalledToDecrementThenOKStatusIsReturned()
            throws Exception {

        // GIVEN
        QuatityDTO quatityDTO = QuatityDTO.builder()
                .quantity(10)
                .build();

        BeerDTO decrementedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        decrementedBeerDTO.setQuantity(decrementedBeerDTO.getQuantity() - quatityDTO.getQuantity());

        // WHEN
        when(beerService.decrement(decrementedBeerDTO.getId(), quatityDTO.getQuantity()))
                .thenReturn(decrementedBeerDTO);

        // THEN
        mockMvc.perform(
                patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJSONString(quatityDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(decrementedBeerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(decrementedBeerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(decrementedBeerDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(decrementedBeerDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledDecrementLessThanMinThenABadRequestStatusIsReturned()
            throws Exception {

        // GIVEN
        QuatityDTO quatityDTO = QuatityDTO.builder()
                .quantity(40)
                .build();

        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        expectedBeerDTO.setQuantity(expectedBeerDTO.getQuantity() - quatityDTO.getQuantity());

        // WHEN
        when(beerService.decrement(VALID_BEER_ID, quatityDTO.getQuantity()))
                .thenThrow(BeerStockNegativeException.class);

        // THROW
        mockMvc.perform(
                patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJSONString(quatityDTO)))
                .andExpect(status().isBadRequest());
    }

}
