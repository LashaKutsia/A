package webservices.exception;

import javax.xml.ws.WebFault;

@WebFault
public class PersonAlreadyExists extends Exception {

    private static final long serialVersionUID = 1L;

    public PersonAlreadyExists() {
        super("This Person already exists");
    }

    public PersonAlreadyExists(String str) {
        super(str);
    }
}
