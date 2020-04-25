/* test */
typedef enum {
    MON,
    TUE,
    WED,
    THUR,
    FRI
} weekday_type;

typedef enum {
    SAT = 5,
    SUN
} weekend_type;

// Global variables
int tmp;
int length = 4, width, height;

// Define functions above other functions call them.
int add(int num1, int num2) {
    return num1 + num2;
}

int conditionals(int x, int y) {
    int j = 2;

    if (x < 3) {
        return 1;
    } else if(y < 4) {
        if(j < 9) {
            j++;
        }
        return j;
    } else {
        return 9;
    }
}

int main(int a, int b, int y) {

    int test = 8 + (9 - 2 + 6);

    int sum;

    // Expression testing
    sum = add(8, 9, 8, 89, 7, 0, 5, 5);

    a = (b * 3) - 2 + 10;
    a++;

    // Goto label
    label:

    int x = 1;
    x = ++x;

    x ? 1 : 0;
    !x;
    ++x + y;
    x++ + y;

    (1 + 2 - -3 / 6);
    x = 3;

    // Conditional & assignment operator testing
    if (a != b) {
        a *= 5;
        b /= 10;
        b -= 20;
        a += 45;
    }

    if(4) {
        while(1+4) {
             // break statement testing
             break;
             int m;
        }

        if (9) {
            int n;
        } else {
            int o;
        }
    }

    // For loop testing
    for (int i; a < y; y++) {
        int h = 9;

        for (int p = 1; p < 8; p++) {
            p++;
        }
    }

    for(; a <= 7; a--) {
        y = 8;
    }

    for(int r; ; r--) {
        r = 34;
    }

    for(int t = 8; t > 9;) {
        t = 34;
    }

    for(;a > b;) {
             b = 55;
         }

    for(;;) {
        y - 9;
        break;
    }

    // Expression testing
    (1 == 2 > 3);
    (1 < 3);
    (1 > 4);
    (1 >= 3);
    (1 <= 2);

    (1 > 2 && 0 < 1);
    (1 > 2 || 0 < 1);

    // goto call
    goto label;

    // variable declaration testing
    int c;
    int d = 7;
    int e, f = 3;
    int g = 10, h = 3;

    int q, j, k;

    // while loop testing
    while(1) {
        j++;
    }

    while(true) {
        q++;
    }

    // return statement testing
    return 1;
}