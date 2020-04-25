int foo(int a, int b) {
    return a + b;
}

int main() {
    int i = 1;
    int j = 5;

    // Broken
    int k = i + 2;

    return foo(i, j);
}