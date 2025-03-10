// Assignment 1 19T3 COMP1511: CS Paint
//
// This program was written by Joel Dean (z5208947)
// Worked on 10/10/19 - 00/00/19
//
// COMP1511 Assignment 1 - Paint 
//
// Does whatever the assignment says it should 
// Command 0 - No input
// Command 1 - Draw a line (Or multiple lines)
// Command 2 - Draw a square

#include <stdio.h>

// library useful for drawing ellipses in Stage 3 and Stage 4.
#include <math.h>

// The dimensions of the canvas (20 rows x 36 columns).
#define N_ROWS 20
#define N_COLS 36

// Shades (assuming your terminal has a black background).
#define BLACK 0
#define DARK 1
#define GREY 2
#define LIGHT 3
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

//Command 1 - Draws a line
void commandOne(int canvas[N_ROWS][N_COLS], int startRow, 
                int startCol, int length, int angle, int shade);


//Command 2 - Draws a Square                
void commandTwo(int canvas[N_ROWS][N_COLS], int startRow, 
                int startCol, int length, int angle, int shade);


int main(void) {
    int canvas[N_ROWS][N_COLS];
    
    clearCanvas(canvas);
    
    int command = 0;
    int program = 1;
    int shade = 0;
    int startRow, startCol, length, angle;
    
    //Continue running until Ctrl+D used
    while (program != EOF) {
   
        //Decipher which command to use
        program = scanf("%d", &command);
        
        //Command 0 - No input
        if (command == 0) {
            program = EOF;
        }
    
        //Command 1 - Draw Line
        if (command == 1) {
        
            //Read in conditions for line
            scanf("%d", &startRow);
            scanf("%d", &startCol);
            scanf("%d", &length);
            scanf("%d", &angle); 
                       
            commandOne(canvas, startRow, startCol, length, angle, shade);
            
        } 
        
        //Command 2 - Fill Square
        if (command == 2) {
            
            //Read in conditions for square
            scanf("%d", &startRow);
            scanf("%d", &startCol);
            scanf("%d", &length);
            scanf("%d", &angle);
            
            commandTwo(canvas, startRow, startCol, length, angle, shade);
            
        } 
        
        //Command 3 - Change Shade
        if (command == 3) {
            
            int colourChange;
            int invalid3 = 0;
            
            //Read in conditions for colour
            scanf("%d", &colourChange);
            
            if (shade + colourChange > 4 || shade + colourChange < 0) {
                invalid3 = 1;
            }
            
            if (invalid3 == 0) {
                shade = shade + colourChange;
            }
            
        }
        
        //Displays canvas when Ctrl+D is used
        if (program == EOF) {
            displayCanvas(canvas);
        }
        
    } // End Command Intake - Ctrl -D
    
    return 0;
} //Main



// ADD CODE FOR YOUR FUNCTIONS HERE

//Draw Line - Command 1
void commandOne(int canvas[N_ROWS][N_COLS], int startRow, 
                int startCol, int length, int angle, int shade) {
    
    //Negative Length - Change direction of line
    if (length < 0) {
        length = length * -1;
        angle += 180;
    }
    
    //Angle is not with the 0-360 range (Put in range)
    if (angle > 360) {
        angle = angle % 360;
    }
    
    int row, col;
    int invalid1 = 0;
    //Convert length to array format (starts at 0)
    length--;
    
    //Check to see if starting postions are valid
    if (startRow < 0 || startRow > 19 ||
        startCol < 0 || startCol > 35) {
        invalid1 = 1;
    }       
    
    //Check to see if line is invalid (Goes outside array bounds)
    if (angle % 360 == 0 && (startRow - length) < 0) {
        invalid1 = 1;
               
    } else if (angle == 270 && (startCol - length) < 0) {
        invalid1 = 1;
        
    } else if (angle == 180 && (startRow + length) > 19) {
        invalid1 = 1;
            
    } else if (angle == 90 && (startCol + length) > 35) {
        invalid1 = 1;
        
    } else if (angle == 45 && 
              ((startCol + length) > 35 || (startRow - length) < 0)) {
        invalid1 = 1;
        
    } else if (angle == 135 &&
              ((startRow + length) > 19 || (startCol + length) > 35)) {
        invalid1 = 1;
        
    } else if (angle == 225 &&
              ((startRow + length) > 19 || (startCol - length) < 0)) {
        invalid1 = 1;
        
    } else if (angle == 315 &&
              ((startRow - length) < 0 || (startCol - length) < 0)) {
        invalid1 = 1;
    }        
    
    //Start the array at specified conditions
    row = startRow;
    col = startCol;
    int counter = 0;
    //Draw Line based on given conditions
    while (counter <= length && length >= 0 && invalid1 == 0) {
        
        // Determine direction of line to be drawn
        // Move to next box in array
        if (angle % 360 == 0) {
            row = startRow - counter;
                
        } else if (angle == 270) {
            col = startCol - counter;
                 
        } else if (angle == 180)  {
            row = startRow + counter;
                
        } else if (angle == 90) {
            col = startCol + counter;
        } else if (angle == 45) {
            row = startRow - counter;
            col = startCol + counter;
            
        } else if (angle == 135) {
            row = startRow + counter;
            col = startCol + counter;
            
        } else if (angle == 225) {
            row = startRow + counter;
            col = startCol - counter;
            
        } else if (angle == 315) {
            row = startRow - counter;
            col = startCol - counter;
        
        //If input angle is invalid    
        } else {
            invalid1 = 1;
        }
        //Write a zero and move to next position
        if (invalid1 == 0) {
            canvas[row][col] = shade;
            counter++;
        }
    } 
} 

//Draw Square - Command 2
void commandTwo(int canvas[N_ROWS][N_COLS], int startRow, 
                int startCol, int length, int angle, int shade) {
                
    int invalid2 = 0;
    
    //Negative Length - Change direction of square
    if (length < 0) {
        length = length * -1;
        angle += 180;
    }
        
    
    //Angle is not with the 0-360 range (Put in range)
    if (angle > 360) {
        angle = angle % 360;
    }
    
    if (angle % 360 == 0 || angle == 270 ||
        angle == 180 || angle == 90) {
        commandOne(canvas, startRow, startCol, length, angle, shade);
        invalid2 = 1;
    }
    
    int topLeftRow, topLeftCol;
    int row, col;
    // Covert length into array format
    length--;
    
    //Check to see if starting positions are valid
    if (startRow < 0 || startRow > 19 ||
        startCol < 0 || startCol > 35) {
        invalid2 = 1;
    }
    
    //Check if square passes array boundary 
    if (angle == 45 && ((startRow - length) < 0 || 
       (startCol + length) > 35)) {
        invalid2 = 1;
            
    } else if (angle == 135 && ((startRow + length) > 19 || 
              (startCol + length) > 35)) {
        invalid2 = 1;
         
    } else if (angle == 225 && ((startRow + length) > 19 || 
              (startCol - length) < 0)) {
        invalid2 = 1;
        
    } else if (angle == 315 && ((startRow - length) < 0 || 
              (startCol - length) < 0)) {
        invalid2 = 1;
    }
    
    // Define variables for drawing square from topleft corner
    topLeftRow = startRow;
    topLeftCol = startCol;
    
    //Set drawing position to top left of square
    if (angle == 45) {
        topLeftRow = startRow - length;
    } else if (angle == 225) {
        topLeftCol = startCol - length;
    } else if (angle == 315) {
        topLeftRow = startRow - length;
        topLeftCol = startCol - length;
    //If angle isn't Valid
    } else if (angle != 135) {
        invalid2 = 1;
    }
    
    //Draw square
    row = topLeftRow;
    int rowCounter = 0;
    while (rowCounter <= length && length >= 0 && invalid2 == 0) {
        
        int colCounter = 0;
        col = topLeftCol;
        while (colCounter <= length) {
            canvas[row][col] = shade;
            col++;
            colCounter++;
        }
        row++;
        rowCounter++;
    }
    
} 


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
