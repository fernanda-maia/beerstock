package one.digitalinnovation.beerstock.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuatityDTO {

    @NotNull
    @Max(100)
    private Integer quantity;
}
