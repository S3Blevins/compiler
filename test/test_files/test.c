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

int tmp;

// another function testing
int foo(int p) {
    int q;
}

int conditionals(int x, int y) {

    int j = 2;

    if(x < 3) {
        return 1;
    } else if(y < 4) {
        if(j < 9) {
            7++;
        }
        return 7;
    } else {
        return 9;
    }
}

int main(int a, int b, int y) {

    //int test = 8 + (9 - 2 + 6);

    //int zdfjhsaldkfjhsdlkafhj;

    //function(7, 6);

    int sum;

    sum = foo(8, 9, 8, 89, 7, 0, 5, 5);

    a = (b * 3) - 2 + 10;
    a++;

    label:

    int x = 1;
    x = ++x;

    x ? 1 : 0;
    !x;
    ++x + y;
    x++ + y;
    // expression testing
    (1 + 2 - -3 / 6);
    x = 3;

    for(int i; i < y; i++) {
        int h = 9;
        (a > h);

        for(int p = 1; p < 8; p++) {
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
    }

    (1 == 2 > 3);
    (1 < 3);
    (1 > 4);
    (1 >= 3);
    (1 <= 2);

    goto label;

    (1 > 2 && 0 < 1);
    (1 > 2 || 0 < 1);

    // variable declaration testing
    int c;
    int d = 7;
    int e, f = 3;
    int g = 10, h = 3;

    int q, j, k;

    // iterative statement testing
    while(1) {
        int l;
    }

    // condition statement testing
    if(4) {
        while(1+4) {
            // break statement testing
            break;
            int m;
        }
    } else if (9) {
        int n;
    } else {
        int o;
    }

    // return statement testing
    return 1;
}

int foobar() {

	int p;
	return 0;
}


