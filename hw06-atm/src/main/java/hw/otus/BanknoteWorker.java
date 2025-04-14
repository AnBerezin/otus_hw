package hw.otus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BanknoteWorker {
    private final CartridgeWorker cartridgeWorker;

    public BanknoteWorker(CartridgeWorker cartridgeWorker) {
        this.cartridgeWorker = cartridgeWorker;
    }

    public void putMoney(List<Banknote> banknoteList) {
        for (Banknote banknote : banknoteList) {
            Cartridge cartridge = cartridgeWorker.getCartridge(banknote.denomination());
            cartridge.putBanknote(banknote);
        }
    }

    public List<Banknote> getMoney(BigDecimal sum) {
        if (!checkSum(sum)) {
            throw new RuntimeException("Не возможно выдать сумму не кратную валютам");
        }
        return cartridgeWorker.getBanknotes(getBanknotesBySum(sum));
    }

    public BigDecimal getBalance() {
        return cartridgeWorker.getCartridgeMap()
                .values()
                .stream()
                .map(Cartridge::getCartridgeBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<Denomination, Integer> getBanknotesBySum(BigDecimal sum) {
        Map<Denomination, Integer> banknotesCount = new HashMap<>();

        int ftCount = sum.divide(BigDecimal.valueOf(5000),RoundingMode.DOWN).intValue();
        if (ftCount > 0) {
            sum = sum.subtract(BigDecimal.valueOf(ftCount).multiply(BigDecimal.valueOf(5000)));
            banknotesCount.put(Denomination.FIVETHOUSAND, ftCount);
        }

        int tCount = sum.divide(BigDecimal.valueOf(1000),RoundingMode.DOWN).intValue();
        if (tCount > 0) {
            sum = sum.subtract(BigDecimal.valueOf(tCount).multiply(BigDecimal.valueOf(1000)));
            banknotesCount.put(Denomination.THOUSAND, tCount);
        }

        int fhCount = sum.divide(BigDecimal.valueOf(500),RoundingMode.DOWN).intValue();
        if (fhCount > 0) {
            sum = sum.subtract(BigDecimal.valueOf(fhCount).multiply(BigDecimal.valueOf(1000)));
            banknotesCount.put(Denomination.FIVEHUNDRED, fhCount);
        }

        int hCount = sum.divide(BigDecimal.valueOf(100),RoundingMode.DOWN).intValue();
        if (hCount > 0) {
            sum = sum.subtract(BigDecimal.valueOf(hCount).multiply(BigDecimal.valueOf(100)));
            banknotesCount.put(Denomination.HUNDRED, hCount);
        }

        int fCount = sum.divide(BigDecimal.valueOf(50),RoundingMode.DOWN).intValue();
        if (fCount > 0) {
            sum = sum.subtract(BigDecimal.valueOf(fCount).multiply(BigDecimal.valueOf(50)));
            banknotesCount.put(Denomination.FIFTY, fCount);
        }

        int tenCount = sum.divide(BigDecimal.valueOf(10),RoundingMode.DOWN).intValue();
        if (tenCount > 0) {
            sum = sum.subtract(BigDecimal.valueOf(tenCount).multiply(BigDecimal.valueOf(10)));
            banknotesCount.put(Denomination.TEN, tenCount);
        }

        if (sum.compareTo(BigDecimal.ZERO) != 0) {
            throw new RuntimeException(String.format("Невозможно выдать сумму: %b", sum));
        }

        return banknotesCount;
    }

    private boolean checkSum(BigDecimal sum) {
        return sum.remainder(BigDecimal.TEN).compareTo(BigDecimal.ZERO) == 0;
    }
}
