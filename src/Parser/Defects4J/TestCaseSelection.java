package Parser.Defects4J;

import java.util.ArrayList;

public class TestCaseSelection {
    private String method;
    private Object selection;
    private Object exclude;

    public String getMethod() {
        return method.toLowerCase().trim();
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getSelection() {
        return selection;
    }
    public void setSelection(Object selection) {
        this.selection = selection;
    }

    public Object getExclude() {
        return exclude;
    }
    public void setExclude(Object exclude) {
        this.exclude = exclude;
    }

    public void parseObjectStructure() throws Exception {
        // Based on the method used we do different things
        if (getMethod().equals("identifier")) {
            System.out.println("identifier");

            if (getExclude() != null) {
                throw new Exception("For method " + getMethod() + " the exclude property is not allowed");
            }

            if (getSelection().getClass() != ArrayList.class) {
                throw new Exception("For method " + getMethod() + " the selection property must be a list of Defects4J identifiers");
            }

            return;

        } else if (getMethod().equals("range")) {
            System.out.println("range");

            if (getExclude() != null) {
                throw new Exception("For method " + getMethod() + " the exclude property is not allowed");
            }

            if (getSelection().getClass() != ArrayList.class) {
                throw new Exception("For method " + getMethod() + " the selection property must be a list of Defects4J identifiers");
            }

            return;


        } else if (getMethod().equals("all")) {
            System.out.println("all");

            // Ensure exclude property is null
            if (getExclude() != null) {
                throw new Exception("For method " + getMethod() + " the exclude property is not allowed");
            }

            // Ensure selection property is excluded
            if (getSelection() != null) {
                throw new Exception("For method " + getMethod() + " the selection property is not allowed");
            }

            return;
        }

        throw  new Exception("The method, " + getMethod() + " is not a valid method");
    }
}