// Assignment 1 19T3 COMP1511: CS Paint
//
// This program was written by Joel Dean (z5208947)
// Worked on 10/10/19 - 00/00/19
//
// Does whatever the assignment says it should 
// Command 0 - No input
// Command 1 - Draw a line (Or multiple lines)
// Command 2 - Draw a square

#include <stdio.h>

// Note: you may find the square root function (sqrt) from the math
// library useful for drawing ellipses in Stage 3 and Stage 4.
#include <math.h>

// The dimensions of the canvas (20 rows x 36 columns).
#define N_ROWS 20
#define N_COLS 36

// Shades (assuming your terminal has a black background).
#define BLACK 0
#define WHITE 4

// Provided helper functions:
// Display the canvas.
void displayCanvas(int canvas[N_ROWS][N_COLS]);

// Clear the canvas by setting every pixel to be white.
void clearCanvas(int canvas[N_ROWS][N_COLS]);

// Calculate the distance between two points.
// Note: you will only need this function for the Draw Ellipse command
// in Stages 3 and 4.
double distance(int row1, int col1, int row2, int col2);


// ADD PROTOTYPES FOR YOUR FUNCTIONS HERE


int main(void) {
    int canvas[N_ROWS][N_COLS];
    
    //Assign all values in array to 4
    clearCanvas(canvas);
    
    int command = 0;
    int program = 1;
    
    //Continue running until Ctrl+D used
    while (program != EOF) {
    
    //Decipher which command to use
    program = scanf("%d", &command);
        
        //Command 0 - No input
        if (command == 0) {
            program = EOF;
        }
    
        //Command 1 - Line Drawing
        if (command == 1) {
        
            int startRow, startCol, length, angle;
            
            //Ask user for condition to draw line
            scanf("%d", &startRow);
            scanf("%d", &startCol);
            scanf("%d", &length);
            scanf("%d", &angle); 
          
            int invalid = 0;
            int row, col;
            
            //Start the array at specified conditions
            row = startRow;
            col = startCol;
            length = length - 1;
            
            //Check to see if line is invalid (Goes outside array bounds)
            if (angle == 0) {
                if ((startRow - length) < 0) {
                    invalid = 1;
                }
            } else if (angle % 360 == 0) {
                if ((startRow - length) < 0) {
                    invalid = 1;
                }    
            } else if (angle % 270 == 0) {
                if ((startCol - length) < 0) {
                    invalid = 1;
                }
            } else if (angle % 180 == 0) {
                if ((startRow + length) >= 20) {
                    invalid = 1;
                }
            } else if (angle % 90 == 0) {
                if ((startCol + length) >= 36) {
                    invalid = 1;
                }
            }       
              
            int counter = 0;
            int angleTest = 0;
            //Draw Line based on given conditions
            while (counter <= length && length > -1 &&
                   invalid == 0 && angleTest != 1) {
                
                // Determine direction of line to be drawn
                // By using modulus and starting from largest angles 
                // (excepting zero)
                // Up/Down is changed by col 
                // Left/Right is changed by row
                if (angle == 0) {
                    row = startRow - counter;
                        
                } else if (angle % 360 == 0) {
                    row = startRow - counter;
                        
                } else if (angle % 270 == 0) {
                    col = startCol - counter;
                         
                } else if (angle % 180 == 0)  {
                    row = startRow + counter;
                        
                } else if (angle % 90 == 0) {
                    col = startCol + counter;
                    
                //If input angle is invalid    
                } else {
                    angleTest = 1;
                }
                //Write a zero and move to next position
                if (angleTest == 0) {
                canvas[row][col] = 0;
                counter++;
                }
            } // Draw line for Command 
        } // Command 1
        
        //Displays canvas when Ctrl+D is used
        displayCanvas(canvas);
        
        
    } // End Command Intake - Ctrl -D
    
    return 0;
} //Main












// ADD CODE FOR YOUR FUNCTIONS HERE



// Displays the canvas, by printing the integer value stored in
// each element of the 2-dimensional canvas array.
//
// You should not need to change the displayCanvas function.
void displayCanvas(int canvas[N_ROWS][N_COLS]) {
    int row = 0;
    while (row < N_ROWS) {
        int col = 0;
        while (col < N_COLS) {
            printf("%d ", canvas[row][col]);
            col++;
        }
        row++;
        printf("\n");
    }
}


// Sets the entire canvas to be blank, by setting each element in the
// 2-dimensional canvas array to be WHITE (which is #defined at the top
// of the file).
//
// You should not need to change the clearCanvas function.
void clearCanvas(int canvas[N_ROWS][N_COLS]) {
    int row = 0;
    while (row < N_ROWS) {
        int col = 0;
        while (col < N_COLS) {
            canvas[row][col] = WHITE;
            col++;
        }
        row++;
    }
}

// Calculate the distance between two points (row1, col1) and (row2, col2).
// Note: you will only need this function for the Draw Ellipse command
// in Stages 3 and 4.
double distance(int row1, int col1, int row2, int col2) {
    int row_dist = row2 - row1;
    int col_dist = col2 - col1;
    return sqrt((row_dist * row_dist) + (col_dist * col_dist));
}
