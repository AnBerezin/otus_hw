package homework;

import java.util.ArrayDeque;
import java.util.Deque;

public class CustomerReverseOrder {

    private final Deque<Customer> deque = new ArrayDeque<>();

    // todo: 2. надо реализовать методы этого класса
    // надо подобрать подходящую структуру данных, тогда решение будет в "две строчки"

    public void add(Customer customer) {
        deque.push(customer);
    }

    public Customer take() {
        return deque.pollFirst(); // это "заглушка, чтобы скомилировать"
    }
}
