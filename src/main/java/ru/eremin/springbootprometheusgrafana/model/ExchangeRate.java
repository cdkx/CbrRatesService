package ru.eremin.springbootprometheusgrafana.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;


@Entity
@Table(name = "exchange_rate", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"currency_id", "rate_date"})
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exchange_rate_seq")
    @SequenceGenerator(
            name = "exchange_rate_seq",
            sequenceName = "exchange_rate_id_seq",
            allocationSize = 50  // ДОЛЖЕН совпадать с batch_size!
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @Column(name = "rate_date", nullable = false)
    private LocalDate rateDate;

    @Column(name = "nominal", nullable = false)
    private Integer nominal;

    @Column(name = "value", precision = 10, scale = 4, nullable = false)
    private BigDecimal value;

    public BigDecimal getVunitRate() {
        if (value == null || nominal == null || nominal == 0) {
            return BigDecimal.ZERO;
        }
        return value.divide(BigDecimal.valueOf(nominal), 10, RoundingMode.HALF_UP);
    }
}
