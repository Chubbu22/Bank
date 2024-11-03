package bank;

public class InvalidInputExeption extends Exception{
    InvalidInputExeption() {

    }
    InvalidInputExeption(String message) {
        super(message);
    }
}
