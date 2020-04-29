int add(int a, int b, int c, int d) {
    return (a + b) + c + d;
}

int main() {
    int a = 10;
    int b = 2;
    int c = 5;
    int d = 1;
    int result = add(a, b, c, d); //18

    d = 10 + 2; // 12

    return result + add(a,b,c,d); //47
}
