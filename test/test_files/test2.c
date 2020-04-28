int div(int a, int b, int c, int d) {
    return (a / b) / c / d;
}

int main() {
    int a = 10;
    int b = 2;
    int c = 5;
    int d = 1;
    int result = div(a, b, c, d);

    return result + div(a,b,c,d)
     + div(a,b,c,d)
     + div(a, b, c, d);
}