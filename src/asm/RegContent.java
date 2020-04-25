package asm;

public class RegContent {

    public String regValAddress;           // The value of the register*
    public int regValInteger;             // The value of the register*

    // (*) If a register has either regValAddress/regValInteger populated then the other
    // field must be emptied since the register cannot handle an address AND value.

    /**
     * Needed upon creation of registers to start output assembly.
     */
    public RegContent() {
        this.regValAddress = null;
        this.regValInteger = 0;
    }

    public RegContent(String address) {
        this.regValAddress = address;
        this.regValInteger = 0;
    }

    public RegContent(int val) {
        this.regValAddress = null;
        this.regValInteger = val;
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
