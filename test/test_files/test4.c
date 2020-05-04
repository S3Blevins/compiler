int sum_three_nums(int num1, int num2, int num3) {
    return num1 + num2 + num3;
}

int main() {
    int a = 4;
    int b = 0;

    int c = 10;

    int k = 0;
    int i = 0;
    int uselessVar;

    while(b < c) {
        if(b / 2 == 3) {
            while(k < 5) {
                if(k == 4) {
                    break;
                }
                ++k;
            }
        }
        ++b;
    }
    a = 1;
    if (a == 0) {
        int sums = sum_three_nums(2, 5, 22);
        uselessVar = sums;
    } else {
        uselessVar = 122;
        for (; i < 5; ++i) {
            --uselessVar;
            for (int l = 0; l < 2; l++) {
                uselessVar /= 2;
                uselessVar *= 2;
            }
        }

        if (a < 2) {
            while (a < c) {
                ++a;
            }
        }
    }

    return uselessVar;
}