package asm;

import java.util.ArrayList;
import java.util.List;

public class RegisterWrapper {

    List<Register> registers;

    /**
     * Init the registers the compiler will keep track of to
     * convert our IR to assembly.
     */
    public RegisterWrapper() {
        this.registers = new ArrayList<>(){{

            // Add all of the registers defined in RegisterName to the array.
            for (int i = 0; i < RegisterName.values().length; i++) {
                add(new Register(RegisterName.values()[i]));
            }

            /*
                Ascii art of how the registers are represented (DELETE IF OR WHEN NEEDED).
                           -----------------------
                   rax     |   INTEGER/ADDRESS   |                      register[0]
                           -----------------------

                           -----------------------
                   rbx     |   INTEGER/ADDRESS   |                      register[1]
                           -----------------------
                    .       .       .       .
                    .       .       .       .
                    .       .       .       .
                    .       .       .       .
                           -----------------------
                   r15     |   INTEGER/ADDRESS   |                      register[15]
                           -----------------------
             */
        }};
    }
}
