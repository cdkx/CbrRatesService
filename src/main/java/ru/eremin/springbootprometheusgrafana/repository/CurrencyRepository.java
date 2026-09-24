package ru.eremin.springbootprometheusgrafana.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.eremin.springbootprometheusgrafana.model.Currency;

import java.util.Optional;


@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findByCbrId(String cbrId);

    Optional<Currency> findByCharCode(String charCode);
}
