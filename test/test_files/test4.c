int main() {
    int a = 4;
    int b = 0;

    int c = 10;

    int k = 0;

    while(b < c) {
        if(b / 2 == 3) {
            while(k < 5) {
                if(k == 4) {
                    break;
                }
                k++;
            }
        }
        b++;
    }

    return k;

}