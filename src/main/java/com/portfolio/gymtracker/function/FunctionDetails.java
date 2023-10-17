package com.portfolio.gymtracker.function;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class FunctionDetails {

    @Size(min=3, max=30)
    @NotNull
    private String title;
    @Size(max = 300)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String image;

}
