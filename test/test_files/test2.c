int foo(int a, int b) {
    return a + b;
}

int main() {

    int j = 8;
    int i = j + 9;

    return i + foo(1 + 2);
}