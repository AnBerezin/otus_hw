package hw.otus;

import java.math.BigDecimal;

public record Banknote(Denomination denomination, BigDecimal value) {
}
