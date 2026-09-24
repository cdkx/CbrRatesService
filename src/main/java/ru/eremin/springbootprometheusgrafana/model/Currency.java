package ru.eremin.springbootprometheusgrafana.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "currency")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "currency_seq")
    @SequenceGenerator(
            name = "currency_seq",
            sequenceName = "currency_id_seq",
            allocationSize = 50  // ДОЛЖЕН совпадать с batch_size!
    )
    private Long id;

    @Column(name = "cbr_id", unique = true, nullable = false)
    private String cbrId;

    @Column(name = "char_code", nullable = false)
    private String charCode;

    @Column(name = "num_code")
    private String numCode;

    @Column(name = "name")
    private String name;
}
