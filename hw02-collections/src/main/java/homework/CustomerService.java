package homework;

import java.util.*;


public class CustomerService {

    // todo: 3. надо реализовать методы этого класса
    // важно подобрать подходящую Map-у, посмотрите на редко используемые методы, они тут полезны

    private final NavigableMap<Customer, String> customersMap = new TreeMap<>(new Comparator<Customer>() {
        @Override
        public int compare(Customer o1, Customer o2) {
            return (int) (o1.getScores() - o2.getScores());
        }

        ;
    });

    public Map.Entry<Customer, String> getSmallest() {
        return copyCustomerEntry(customersMap.firstEntry());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        return  copyCustomerEntry(customersMap.higherEntry(customer));

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
