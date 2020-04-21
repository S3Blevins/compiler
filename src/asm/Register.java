package asm;

public class Register {

    public RegisterName registerName;       // The name of the register
    public String regValAddress;           // The value of the register*
    public int regValInteger;             // The value of the register*

    // (*) If a register has either regValAddress/regValInteger populated then the other
    // field must be emptied since the register cannot handle an address AND value.

    /**
     * Needed upon creation of registers to start output assembly.
     * @param registerName Differentiates each register by name.
     */
    public Register(RegisterName registerName) {
        this.registerName = registerName;
        this.regValAddress = null;
        this.regValInteger = 0;
    }

    /**
     * This method assigns the register to hold an address String.
     * Once this happens it assigns the regValInteger to zero since
     * there is no integer value but an address that is populating.
     * @param address The address the register will hold.
     */
    public void assignRegAddress(String address) {
        this.regValAddress = address;
        this.regValInteger = 0;
    }

    /**
     * The opposite of assignRegAddress() method
     * @param val The value the register will be populated with.
     */
    public void assignRegInt(int val) {
        this.regValAddress = null;
        this.regValInteger = val;
    }

    //TODO: MIGHT NEED TO SUPPORT DE-REFERENCING ADDRESSES TO GET VALUE???

    /*      0x0
            0x4      -->  j  4(rbp)
            0x8      -->  i  8(rbp)




     */
}
