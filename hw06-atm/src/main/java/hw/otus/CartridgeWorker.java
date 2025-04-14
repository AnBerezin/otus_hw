package hw.otus;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class CartridgeWorker {
    private final Map<Denomination, Cartridge> cartridgeMap = new HashMap<>();
    private static final int CARTRIDGE_CAPACITY = 1000;

    public Cartridge getCartridge(Denomination denomination) {
        return cartridgeMap.computeIfAbsent(denomination, d -> new Cartridge(d, CARTRIDGE_CAPACITY));
    }

    public Map<Denomination, Cartridge> getCartridgeMap() {
        return Collections.unmodifiableMap(cartridgeMap);
    }

    public Map<Denomination, Cartridge> getNonEmptyCartridge() {
        return Collections.unmodifiableMap(cartridgeMap.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public List<Banknote> getBanknotes(Map<Denomination, Integer> requestBanknotes) {
        List<Banknote> result = new ArrayList<>();
        for (Map.Entry<Denomination, Integer> request : requestBanknotes.entrySet()) {
            Cartridge cartridge = cartridgeMap.get(request.getKey());
            if (cartridge == null) {
                throw new RuntimeException(String.format("Невозможно выдать сумму, т.к. картритдж %s пустой", request.getKey()));
            }
            result.addAll(cartridge.getBanknotesFromCartridge(request.getValue()));
        }
        return result;
    }
}
