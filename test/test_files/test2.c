int div(int a, int b, int c, int d) {
    return (a / b) / c / d;
}

int main() {
    int a = 20;
    int b = 2;
    int c = 5;
    int result = div(a, b, c, 2);

    return result + div(20,10,2,2) + div(200,25,4,1);
}