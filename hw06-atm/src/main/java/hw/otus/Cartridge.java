package hw.otus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Cartridge {
    private final Denomination denomination;
    private final int banknotesCapacity;
    private List<Banknote> banknoteList;

    public Cartridge(Denomination denomination, int capacity) {
        this.denomination = denomination;
        this.banknotesCapacity = capacity;
        banknoteList = new ArrayList<>(capacity);
    }

    public int getBanknotesCount() {
        return banknoteList.size();
    }

    public BigDecimal getCartridgeBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        if (banknoteList.isEmpty()) return balance;
        balance = balance.add(BigDecimal.valueOf(banknoteList.size()).multiply(banknoteList.getFirst().value()));
        return balance;
    }

    public void putBanknote(Banknote banknote) {
        if (this.banknoteList.size() == banknotesCapacity) {
            throw new RuntimeException("Cannot add banknotes. Cartridge is full.");
        }
        this.banknoteList.add(banknote);

    }

    public List<Banknote> getBanknotesFromCartridge(int count) {
        if (banknoteList.isEmpty()) {
            return null;
        }
        if (count > banknoteList.size()) {
            throw new RuntimeException(String.format("Не достаточно количество купюр в картиджи %s", denomination));
        }
        List<Banknote> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(banknoteList.remove(i));
        }
        return result;
    }

    public boolean isEmpty() {
        return banknoteList.isEmpty();
    }
}
