package webservices.exception;

import javax.xml.ws.WebFault;

@WebFault
public class PersonNotFound extends Exception {

    private static final long serialVersionUID = 1L;

    public PersonNotFound() {
        super("The specified person does not exist");
    }

    public PersonNotFound(String str) {
        super(str);
    }

}
