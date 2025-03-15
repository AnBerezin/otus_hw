package homework;

import java.util.*;


public class CustomerService {

    // todo: 3. надо реализовать методы этого класса
    // важно подобрать подходящую Map-у, посмотрите на редко используемые методы, они тут полезны

    private final SortedMap<Customer, String> customersMap = new TreeMap<>(new Comparator<Customer>() {
        @Override
        public int compare(Customer o1, Customer o2) {
            return (int) (o1.getScores() - o2.getScores());
        }

        ;
    });

    public Map.Entry<Customer, String> getSmallest() {
        // Возможно, чтобы реализовать этот метод, потребуется посмотреть как Map.Entry сделан в jdk
        Map.Entry<Customer, String> resultEntry = customersMap.entrySet().stream().findFirst().orElse(null);
        return copyCustomerEntry(customersMap.entrySet().stream().findFirst().orElse(null));
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        SortedMap<Customer, String> greaterMap = customersMap.tailMap(customer);
        return copyCustomerEntry(greaterMap.entrySet().stream().filter(entry -> entry.getKey().getId() != customer.getId()).findFirst().orElse(null));

    }

    public void add(Customer customer, String data) {
        customersMap.put(customer, data);
    }

    private Map.Entry<Customer, String> copyCustomerEntry(Map.Entry<Customer, String> entry) {
        if (entry != null) {
            Customer customer = entry.getKey();
            return Map.entry(new Customer(customer.getId(), customer.getName(), customer.getScores()), entry.getValue());
        }
        return null;
    }
}
