int add(int a, int b) {
    return a + b;
}

int div(int a, int b, int c) {
    return (a / b) / add(c, 3);
}

int main() {
    int a = 20;
    int b = 2;
    int c = 5;
    int result = div(a, b, c);

    return result + div(a,b,c);
}
