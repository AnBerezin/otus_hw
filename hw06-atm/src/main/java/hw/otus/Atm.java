package hw.otus;

import java.math.BigDecimal;
import java.util.List;

public class Atm {
    private final BanknoteWorker banknoteWorker;
    private final CartridgeWorker cartridgeWorker;

    public Atm() {
        this.cartridgeWorker = new CartridgeWorker();
        this.banknoteWorker = new BanknoteWorker(this.cartridgeWorker);
    }

    public void putMoney(List<Banknote> banknoteList) {
        if (banknoteList != null && !banknoteList.isEmpty()) {
            banknoteWorker.putMoney(banknoteList);
        }
    }

    public List<Banknote> getMoney(BigDecimal sum) {
        return banknoteWorker.getMoney(sum);
    }

    public BigDecimal getBalance() {
        return banknoteWorker.getBalance();
    }
}
