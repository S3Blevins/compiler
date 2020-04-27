package asm;

public enum RegName {
        edi(false),
        esi(false),
        edx(false),
        ecx(false),
        r8d(false),
        r9d(false),
        eax(false),
        ebx(false),
        rbp(false),
        rsp(false),
        r10d(false),
        r11d(false);

        public boolean lock;

        RegName(boolean b) {
                this.lock = b;
        }
}

