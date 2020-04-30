int main() {
    int a = 3;
    int b = 0;

    for(int i = 0; i < a; i++) {
        b++;
    }

// a < i    | 3 < 0
//i++       | i = 1
// a < i    | 3 < 1
//i++       | i = 2
// a < i    | 3 < 2
//i++       | i = 3
// a < i    | 3 < 3
//i++       | i = 4
// a < i    | 3 < 4

//


    int c = 0;

    while(c < a) {
        c++;
    }

    return b;

}