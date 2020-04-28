package asm;

public class memContent {
    public String nameRef;
    public boolean lock;
    public String var;

    public memContent(String name, String var) {
        this.nameRef = name;
        this.lock = false;
        this.var = var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public String getName() {
        try {
            int tmp = Integer.parseInt(nameRef) + 1;
            return "-" + (tmp * 4) + "(%rbp)";
        } catch (NumberFormatException e) {
            return "%" + nameRef;
        }
    }

    /**
     * This method assigns the register to hold an address String.
     * Once this happens it assigns the regValInteger to zero since
     * there is no integer value but an address that is populating.
     * @param address The address the register will hold.
    public void assignRegAddress(String address) {
        this.regValAddress = address;
        this.regValInteger = 0;
    }
     */

    /**
     * The opposite of assignRegAddress() method
     * @param val The value the register will be populated with.
    public void assignRegInt(int val) {
        this.regValAddress = null;
        this.regValInteger = val;
    }
*/
    //TODO: MIGHT NEED TO SUPPORT DE-REFERENCING ADDRESSES TO GET VALUE???

}
