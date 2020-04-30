int dec(int a) {
    return a--;
}

int inc(int a) {
    return a++;
}

int div(int a, int b, int c, int d) {
    return (a / b) / c / d;
}

int mul(int a, int b, int c, int d) {
    return (a * b) * c * d;
}

int sub(int a, int b, int c, int d) {
    return (a - b) - c - d;
}

int add(int a, int b, int c, int d) {
    return (a + b) + c + d;
}

int main() {
    int a = 10;
    int b = 2;
    int c = 5;
    int d = 1;
    int add1 = add(a, b, c, d);
    a += 1;
    int sub1 = sub(a, b, c, d);
    b *= a;
    int mul1 = mul(a, b, c, d);
    c -= b;
    int div1 = div(a, b, c, d);

    int result = add1 + sub1 + mul1 + div1;

    d += 11;

    int add2 = add(6, 5, 4, 3);

    b += b + 4;

    int sub2 = sub(10, 5, 1, d);

    c *= 2;

    int mul2 = mul(2, 5, b, 3);

    a -= 6;

    int div2 = div(80, c, 4, 1);

    d /= 2;

    return result / d + add2 - sub2 * div2 + mul2 ;
}
