//Just a test
// Joel Dean

#include <stdio.h>

int function(int test);

int main(void) {

    int test;
    
    test = 0;
    
    function(test);
    
    printf("%d", function(test));
    
    return 0;
}
    
    
int function(int test) {
    test = 1;
    return test;
}


    
