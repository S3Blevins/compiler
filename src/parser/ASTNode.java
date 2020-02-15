package parser;

import java.lang.reflect.Field;

public abstract class ASTNode {

    public void printNode(){
        Field[] fields = this.getClass().getDeclaredFields();

        // iterate through object attributes
        for(Field field: fields) {
            try {
                // if the attribute is a production rule object
                // recursively go further, otherwise print out the terminal
                if(field.get(this) instanceof ASTNode) {
                    ((ASTNode) field.get(this)).printNode();
                } else {
                    System.out.println(field.get(this));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //TODO: create wrapper function for printNode()

}

