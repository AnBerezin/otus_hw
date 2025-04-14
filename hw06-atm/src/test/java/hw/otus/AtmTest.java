package hw.otus;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AtmTest {
    private Atm atm;

    @BeforeEach
    public void setup() {
        atm = new Atm();
        Banknote ten = new Banknote(Denomination.TEN, BigDecimal.TEN);
        Banknote hundred = new Banknote(Denomination.HUNDRED, BigDecimal.valueOf(100));
        List<Banknote> banknoteList = new ArrayList<>();
        Collections.addAll(banknoteList, ten, hundred);
        atm.putMoney(banknoteList);
    }

    @Test
    public void shouldCheckBalanceTest() {
        BigDecimal balance = BigDecimal.valueOf(110);
        Assertions.assertEquals(balance, atm.getBalance());
    }

    @Test
    public void shouldGetMoneyBySum() {
        List<Banknote> banknotes = atm.getMoney(BigDecimal.valueOf(110));
        Assertions.assertEquals(BigDecimal.valueOf(110), banknotes.stream()
                .map(Banknote::value)
                .reduce(BigDecimal.ZERO,BigDecimal::add));
    }

    @Test
    public void shouldThrowExceptionOnGetMoneyBySum() {
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> atm.getMoney(BigDecimal.valueOf(111)));
        Assertions.assertEquals("Не возможно выдать сумму не кратную валютам", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnGetTooMuchMoney() {
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> atm.getMoney(BigDecimal.valueOf(1000)));
        Assertions.assertEquals("Невозможно выдать сумму, т.к. картритдж THOUSAND пустой", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnGetTwoHundred() {
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> atm.getMoney(BigDecimal.valueOf(200)));
        Assertions.assertEquals("Не достаточно количество купюр в картиджи HUNDRED", exception.getMessage());
    }
}
