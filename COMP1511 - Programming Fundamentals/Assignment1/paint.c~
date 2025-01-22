// Assignment 1 19T3 COMP1511: CS Paint
//
// This program was written by Joel Dean (z5208947)
// Worked on 10/10/19 - 27/10/19
//
// COMP1511 Assignment 1 - Paint 
//
// Does whatever the assignment says it should 
// Command -1 - No input
// Command 0 - Draw an Ellipse
// Command 1 - Draw a line
// Command 2 - Draw a square
// Command 3 - Change Shade
// Command 4 - Copy and Paste within the canvas


#include <stdio.h>

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
double distance(int row1, int col1, int row2, int col2);

//Command 1 - Draws a line
void commandOne(int canvas[N_ROWS][N_COLS], int startRow, 
                int startCol, int length, int angle, int shade, 
                int copy[N_ROWS][N_COLS], int command);

//Command 2 - Draws a Square                
void commandTwo(int canvas[N_ROWS][N_COLS], int startRow, 
                int startCol, int length, int angle, int shade,
                int copy[N_ROWS][N_COLS], int command);
                
//Command 4 - Copies a part of canvas                
void commandFour(int canvas[N_ROWS][N_COLS], int startRow, 
                int startCol, int length, int angle,
                int copy[N_ROWS][N_COLS], int pasteRow, int pasteCol);
                
//Command 4 - Pastes copied part to another part of canvas                
void pasteLine(int canvas[N_ROWS][N_COLS], int startRow, int startCol,
               int length, int angle, int copy[N_ROWS][N_COLS],
               int pasteRow, int pasteCol);
               
// Command 0 - Draws an Ellipse
void drawEllipse(int canvas[N_ROWS][N_COLS], int shade, int row1, 
                 int col1, int row2, int col2, 
                 double length, int fill, int ecopy[N_ROWS][N_COLS]);

//Command 0 - Draws a Hollow Ellipse                
void hollow(int shade, int canvas[N_ROWS][N_COLS], int ecopy[N_ROWS][N_COLS]);


int main(void) {

    // Canvas is original | Copy is for calculations
    int canvas[N_ROWS][N_COLS];
    int copy[N_ROWS][N_COLS];
    
    clearCanvas(canvas);
    
    int command = -1;
    int program = 1;
    int shade = 0;
    int startRow, startCol, length, angle;
    int pasteRow, pasteCol;
    
    //Continue running until Ctrl+D used
    while (program != EOF) {
   
        //Decipher which command to use
        program = scanf("%d", &command);
        
        //Command -1 - No input
        if (command == -1) {
            program = EOF;
        }
    
        //Command 1 - Draw Line
        if (command == 1 && program != EOF) {
        
            //Read in conditions for line
            scanf("%d", &startRow);
            scanf("%d", &startCol);
            scanf("%d", &length);
            scanf("%d", &angle); 
            
            //Draws Line           
            commandOne(canvas, startRow, startCol, length, angle, shade, copy,
                       command);
            
        } 
        
        //Command 2 - Fill Square
        if (command == 2 && program != EOF) {
            
            //Read in conditions for square
            scanf("%d", &startRow);
            scanf("%d", &startCol);
            scanf("%d", &length);
            scanf("%d", &angle);
            
            //Draw Square
            commandTwo(canvas, startRow, startCol, length, angle, shade, copy,
                       command);
            
        } 
        
        //Command 3 - Change Shade
        if (command == 3 && program != EOF) {
            
            int invalid3 = 0;
            int colourChange;
            
            //Read in conditions for colour
            scanf("%d", &colourChange);
            
            //Change Shade
            if (shade + colourChange > 4 || shade + colourChange < 0) {
                invalid3 = 1;
                
            } else if (invalid3 == 0) {
                shade = shade + colourChange;
            }
            
        }
        
        if (command == 4 && program != EOF) {
            
            //Read in conditions for square
            scanf("%d", &startRow);
            scanf("%d", &startCol);
            scanf("%d", &length);
            scanf("%d", &angle);
            scanf("%d", &pasteRow);
            scanf("%d", &pasteCol);
            
            //Copy from canvas
            commandTwo(canvas, startRow, startCol, length, angle, shade, copy,
                       command);
                       
            //Print to Canvas
            commandFour(canvas, startRow, startCol, length, angle, copy,
                        pasteRow, pasteCol);
        
        }
        
        //Command 0 - Draw Ellipse
        if (command == 0 && program != EOF) {
            
            int row1, col1, row2, col2, fill;
            double elength = 0.0;
            int ecopy[N_ROWS][N_COLS];
            
            //Read in parameters for ellipse
            scanf("%d", &row1);
            scanf("%d", &col1);
            scanf("%d", &row2);
            scanf("%d", &col2);
            scanf("%lf", &elength);
            scanf("%d", &fill);
            
            //Draws an Ellipse
            drawEllipse(canvas, shade, row1, col1, row2, col2, elength, 
                        fill, ecopy);
            
        }
        
        //Displays canvas when Ctrl+D is used
        if (program == EOF) {
            displayCanvas(canvas);
        }
        
    } 
    
    return 0;
} 

//Draw Line - Command 1
void commandOne(int canvas[N_ROWS][N_COLS], int startRow, 
                int startCol, int length, int angle, int shade,
                int copy[N_ROWS][N_COLS], int command) {
    
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
    //Draw the Line
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
        
        if (invalid1 == 0) {
            //If Command 4 - Copy instead of print
            if (command == 4) {
                copy[row][col] = canvas[row][col];
                
            } else {
                canvas[row][col] = shade;
                counter++;
            }
        }
    } 
} 

//Draw Square - Command 2
void commandTwo(int canvas[N_ROWS][N_COLS], int startRow, 
                int startCol, int length, int angle, int shade, 
                int copy[N_ROWS][N_COLS], int command) {
                
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
    
    //Perform Command 1 if Angle = 0/90/180/270/360
    if (angle % 360 == 0 || angle == 270 ||
        angle == 180 || angle == 90) {
        commandOne(canvas, startRow, startCol, length, angle, shade, copy,
                   command);
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
    
    // Set drawing position to top left of square
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
            //If Command 4 - Copy instead of print
            if (command == 4) {
                copy[row][col] = canvas[row][col];
            } else {
                canvas[row][col] = shade;
            }
            col++;
            colCounter++;
        }
        row++;
        rowCounter++;
    }
    
} 

//Paste the copied square/line onto the paste location
void commandFour(int canvas[N_ROWS][N_COLS], int startRow, int startCol,
                 int length, int angle, int copy[N_ROWS][N_COLS],
                 int pasteRow, int pasteCol) {
                 
    int invalid4 = 0;
    
    //Negative Length - Change direction of square
    if (length < 0) {
        length = length * -1;
        angle += 180;
    }
        
    
    //Angle is not with the 0-360 range (Put in range)
    if (angle > 360) {
        angle = angle % 360;
    }
    
    //Paste a Line if Angle = 0/90/180/270/360
    if (angle % 360 == 0 || angle == 270 ||
        angle == 180 || angle == 90) {
        
        //Paste in a line a specified location
        pasteLine(canvas, startRow, startCol, length, angle, copy,
                  pasteRow, pasteCol);
        invalid4 = 1;
    }
    
    
    int topLeftRow, topLeftCol;
    int row, col;
    int pasteTLRow, pasteTLCol;
    int currentRow, currentCol;
    // Covert length into array format
    length--;
    
    //Check to see if starting positions are valid - Copy
    if (startRow < 0 || startRow > 19 ||
        startCol < 0 || startCol > 35) {
        invalid4 = 1;
    }
    
    //Check to see if starting positions are valid - Paste
    if (pasteRow < 0 || pasteRow > 19 ||
        pasteCol < 0 || pasteCol > 35) {
        invalid4 = 1;
    }
    
    //Check if square passes array boundary - Copy Array
    if (angle == 45 && ((startRow - length) < 0 || 
       (startCol + length) > 35)) {
        invalid4 = 1;
            
    } else if (angle == 135 && ((startRow + length) > 19 || 
              (startCol + length) > 35)) {
        invalid4 = 1;
         
    } else if (angle == 225 && ((startRow + length) > 19 || 
              (startCol - length) < 0)) {
        invalid4 = 1;
        
    } else if (angle == 315 && ((startRow - length) < 0 || 
              (startCol - length) < 0)) {
        invalid4 = 1;
    }
    
    //Check if square passes array boundary - Paste Array
    if (angle == 45 && ((pasteRow - length) < 0 || 
       (pasteCol + length) > 35)) {
        invalid4 = 1;
            
    } else if (angle == 135 && ((pasteRow + length) > 19 || 
              (pasteCol + length) > 35)) {
        invalid4 = 1;
         
    } else if (angle == 225 && ((pasteRow + length) > 19 || 
              (pasteCol - length) < 0)) {
        invalid4 = 1;
        
    } else if (angle == 315 && ((pasteRow - length) < 0 || 
              (pasteCol - length) < 0)) {
        invalid4 = 1;
    }
    
    // Define variables for drawing square from topleft corner
    topLeftRow = startRow;
    topLeftCol = startCol;
    pasteTLRow = pasteRow;
    pasteTLCol = pasteCol;
    
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
        invalid4 = 1;
    }
    
    //Set drawing position to top left of square
    if (angle == 45) {
        pasteTLRow = pasteRow - length;
    } else if (angle == 225) {
        pasteTLCol = pasteCol - length;
    } else if (angle == 315) {
        pasteTLRow = pasteRow - length;
        pasteTLCol = pasteCol - length;
        //If angle isn't Valid
    } else if (angle != 135) {
        invalid4 = 1;
    }
    
    // Paste square
    row = topLeftRow;
    currentRow = pasteTLRow;
    int rowCounter = 0;
    while (rowCounter <= length && length >= 0 && invalid4 == 0) {
        
        int colCounter = 0;
        col = topLeftCol;
        currentCol = pasteTLCol;
        while (colCounter <= length) {
            canvas[currentRow][currentCol] = copy[row][col];
        
            col++;
            currentCol++;
            colCounter++;
        }
        row++;
        currentRow++;
        rowCounter++;
        
    
    } 
                 
}

//Pastes a line onto Canvas Array (Only used inside CommandFour function)
void pasteLine(int canvas[N_ROWS][N_COLS], int startRow, int startCol,
               int length, int angle, int copy[N_ROWS][N_COLS],
               int pasteRow, int pasteCol) {
               
    int invalid4 = 0;
    int currentRow, currentCol;
    int row, col;
    
    //Check to see if starting postions are valid - Copy
    if (startRow < 0 || startRow > 19 ||
        startCol < 0 || startCol > 35) {
        invalid4 = 1;
    }
    
    //Check to see if starting postions are valid - Paste
    if (pasteRow < 0 || pasteRow > 19 ||
        pasteCol < 0 || pasteCol > 35) {
        invalid4 = 1;
    }              
    
    //Check to see if line is invalid (Goes outside array bounds) - Copy
    if (angle % 360 == 0 && (startRow - length) < 0) {
        invalid4 = 1;
               
    } else if (angle == 270 && (startCol - length) < 0) {
        invalid4 = 1;
        
    } else if (angle == 180 && (startRow + length) > 19) {
        invalid4 = 1;
            
    } else if (angle == 90 && (startCol + length) > 35) {
        invalid4 = 1;
    }
    
    //Check to see if line is invalid (Goes outside array bounds) - Paste
    if (angle % 360 == 0 && (pasteRow - length) < 0) {
        invalid4 = 1;
               
    } else if (angle == 270 && (pasteCol - length) < 0) {
        invalid4 = 1;
        
    } else if (angle == 180 && (pasteRow + length) > 19) {
        invalid4 = 1;
            
    } else if (angle == 90 && (pasteCol + length) > 35) {
        invalid4 = 1;
    }
    
    //Start the array at specified conditions
    currentRow = pasteRow;
    currentCol = pasteCol;
    row = startRow;
    col = startCol;
    int counter = 0;
    //Draw Line based on given conditions
    while (counter <= length && length >= 0 && invalid4 == 0) {
        
        // Determine direction of line to be drawn
        // Move to next box in array
        if (angle % 360 == 0) {
            row = startRow - counter;
            currentRow = pasteRow - counter;
                
        } else if (angle == 270) {
            col = startCol - counter;
            currentCol = pasteCol - counter;
                 
        } else if (angle == 180)  {
            row = startRow + counter;
            currentRow = pasteRow + counter;
                
        } else if (angle == 90) {
            col = startCol + counter;
            currentCol = pasteCol + counter;
        
            //If input angle is invalid    
        } else {
            invalid4 = 1;
        }
        //Write a zero and move to next position
        if (invalid4 == 0) {
            canvas[currentRow][currentCol] = copy[row][col];
            
        }
    }     
                 
}

//Draw a hollow or filled in Ellipse
void drawEllipse(int canvas[N_ROWS][N_COLS], int shade, int row1, int col1, 
                 int row2, int col2, double length, int fill, 
                 int ecopy[N_ROWS][N_COLS]) {
    
    //Go through whole array
    int currentRow = 0;
    while (currentRow < N_ROWS) {
        
        int currentCol = 0;
        while (currentCol < N_COLS) {
            
            double dist1 = distance(row1, col1, currentRow, currentCol);
            double dist2 = distance(row2, col2, currentRow, currentCol);
            
            //Draw a filled in Ellipse
            if (dist1 + dist2 <= 2 * length 
                && (fill < 0 || fill > 0)) {
                
                canvas[currentRow][currentCol] = shade;
            }
            
            //Fill copy canvas with 4's
            if (fill == 0) {
                ecopy[currentRow][currentCol] = 4;
            }
            
            //Draw a filled ellipse into copy canvas
            if (dist1 + dist2 <= 2 * length 
                && fill == 0) {
                ecopy[currentRow][currentCol] = 1; 
            }
            currentCol++;
        }
        currentRow++;
    }
    
    //Draw a Hollow Ellipse
    if (fill == 0) {
        hollow(shade, canvas, ecopy);
    }                 
}

//Draws the Hollow Elipse
void hollow(int shade, int canvas[N_ROWS][N_COLS], int ecopy[N_ROWS][N_COLS]) {
    int row = 0;
    while (row < N_ROWS) {
    
        int col = 0;
        while (col < N_COLS) { 
           
            //Narrow down to only edges of ellipse
            //Check Corners of array
            if (ecopy[row][col] == 1) {
            
                if (row == 0 && col == 0) {
                    if ((ecopy[row + 1][col] == 4 || ecopy[row][col + 1] == 4)) {
                        canvas[row][col] = shade; 
                    } 
                } else if (row == 0 && col == 35) {
                    if (ecopy[row + 1][col] == 4 || ecopy[row][col - 1] == 4) {
                        canvas[row][col] = shade;
                    }
                } else if (row == 19 && col == 0) {
                    if (ecopy[row - 1][col] == 4 || ecopy[row][col + 1] == 4) {
                        canvas[row][col] = shade;
                    }
                } else if (row == 19 && col == 35) {
                    if (ecopy[row - 1][col] == 4 || ecopy[row][col - 1] == 4) {  
                        canvas[row][col] = shade;                    
                    }
                } else if (row == 0) {
                    if (ecopy[row + 1][col] == 4 || ecopy[row][col + 1] == 4 ||
                        ecopy[row][col - 1] == 4) {
                        canvas[row][col] = shade;
                    }
                    //Check line ends of array
                } else if (col == 0) {
                    if (ecopy[row + 1][col] == 4 || ecopy[row - 1][col] == 4 ||
                        ecopy[row][col + 1] == 4) {
                        canvas[row][col] = shade;
                    }
                } else if (row == 19) {
                    if (ecopy[row - 1][col] == 4 || ecopy[row][col - 1] == 4 ||
                        ecopy[row][col + 1] == 4) {
                        canvas[row][col] = shade;
                    }
                } else if (col == 35) {
                    if (ecopy[row - 1][col] == 4 || ecopy[row + 1][col] == 4 ||
                        ecopy[row][col - 1] == 4) {
                        canvas[row][col] = shade;
                    }
                    //Check the inside of the array
                } else if (ecopy[row + 1][col] == 4 || ecopy[row - 1][col] == 4 || 
                           ecopy[row][col + 1] == 4 || ecopy[row][col - 1] == 4) {
                    canvas[row][col] = shade;
                }
            }
            col++;  
        }
        row++;
    }
}

// Displays the canvas, by printing the integer value stored in
// each element of the 2-dimensional canvas array.
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
double distance(int row1, int col1, int row2, int col2) {
    int row_dist = row2 - row1;
    int col_dist = col2 - col1;
    return sqrt((row_dist * row_dist) + (col_dist * col_dist));
}
