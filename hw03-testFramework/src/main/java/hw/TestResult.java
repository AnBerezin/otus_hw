package hw;

public class TestResult {
    private String testName;
    private TestStatus status;
    private Exception exception;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public TestStatus getStatus() {
        return status;
    }

    public void setStatus(TestStatus status) {
        this.status = status;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "testName='" + testName + '\'' +
                ", status=" + status +
                ", exception=" + exception;
    }
}
