package one.digitalinnovation.beerstock.service;

import lombok.AllArgsConstructor;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.dto.QuatityDTO;
import one.digitalinnovation.beerstock.entity.Beer;
import one.digitalinnovation.beerstock.exception.BeerAlreadyRegisteredException;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.exception.BeerStockExceededException;
import one.digitalinnovation.beerstock.mapper.BeerMapper;
import one.digitalinnovation.beerstock.repository.BeerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper = BeerMapper.INSTANCE;

    public BeerDTO createBeer(BeerDTO beerDTO)
            throws BeerAlreadyRegisteredException {

        verifyIfIsRegistered(beerDTO.getName());

        Beer beerToCreate = beerMapper.toModel(beerDTO);
        Beer savedBeer = beerRepository.save(beerToCreate);

        return beerMapper.toDTO(savedBeer);
    }

    public BeerDTO findByName(String name) throws BeerNotFoundException {
        Beer foundBeer = beerRepository.findByName(name)
                .orElseThrow(() -> new BeerNotFoundException(name));

        return beerMapper.toDTO(foundBeer);
    }

    public List<BeerDTO> listAll() {
        return beerRepository.findAll()
                .stream()
                .map(beerMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws BeerNotFoundException {
        verifyIfExists(id);
        beerRepository.deleteById(id);
    }

    public BeerDTO increment(Long id, int quantity)
            throws BeerNotFoundException, BeerStockExceededException {
        Beer beerToIncrementStock = verifyIfExists(id);

        int totalQuantity = beerToIncrementStock.getQuantity() + quantity;

        if(totalQuantity > beerToIncrementStock.getMax()) {
            throw new BeerStockExceededException();
        }

        beerToIncrementStock.setQuantity(totalQuantity);
        Beer beerIncremented = beerRepository.save(beerToIncrementStock);

        return beerMapper.toDTO(beerIncremented);
    }

    private void verifyIfIsRegistered(String beerName)
            throws BeerAlreadyRegisteredException {

        Optional<Beer> isRegistered = beerRepository.findByName(beerName);

        if(isRegistered.isPresent()) {
            throw new BeerAlreadyRegisteredException(beerName);
        }
    }

    private Beer verifyIfExists(Long id) throws BeerNotFoundException {
        return beerRepository.findById(id)
                .orElseThrow(BeerNotFoundException::new);

    }

}
