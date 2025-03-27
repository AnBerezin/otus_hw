package hw;

public class CustomTest {

    @Before
    public void beforeMethod() {
        System.out.println("\n@beforeMethod. ");
        System.out.printf("Экземпляр тестового класса: %s%n", Integer.toHexString(hashCode()));
    }

    @Test
    public void anyTest1() {
        System.out.println("@Test: anyTest1. ");
        System.out.printf("Экземпляр тестового класса: %s%n", Integer.toHexString(hashCode()));
    }

    @Test
    public void anyTest2() {
        System.out.println("@Test: anyTest2. ");
        System.out.printf("Экземпляр тестового класса: %s%n", Integer.toHexString(hashCode()));
    }

    @Test
    public void anyTest3() {
        throw new RuntimeException("Error message");
    }

    @After
    public void afterMethod() {
        System.out.println("@afterMethod. ");
        System.out.printf("Экземпляр тестового класса: %s%n", Integer.toHexString(hashCode()));
    }
}
