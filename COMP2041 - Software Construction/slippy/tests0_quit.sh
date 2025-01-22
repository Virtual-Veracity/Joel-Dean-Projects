#! /bin/dash
# ==================    COMP 2041  ====================
#                     Written By - z5208947
# Tests are assumed to run in a the proper directory

# ===============================    Tests for subset 0 - Quit Command  ===================================
#
#
#  ---------  slippy-quit ------------
echo "Beginning slippy Quit Testing"

# Tests
# Test 1: Invalid Argument Check
#       a: No command
#       b: Only one '/' slash
#       c: Space in between args
#       d: Letters instead of num
# Test 2: Line Number 
#       a: Start 
#       b: middle
#       c: End 

# Test 3: Regex
#       a: Number
#       b: Position
#       c: Weird char

# Test 1: Invalid Argument Check
#       a: No command
output=$(./slippy)
if test output == "usage: slippy [-i] [-n] [-f <script-file> | <sed-command>] [<files>...]" -a $? -eq 1
then
    echo 'Test 1a Passed'
else
    echo 'Test 1a Failed'
fi

# Test 2b: Only one '/' slash
output=$(./slippy /^10q)
if test output == "slippy: command line: invalid command" -a $? -eq 1
then
    echo 'Test 1b Passed'
else
    echo 'Test 1b Failed'
fi

#       c: Space in between args
output=$(./slippy /^10/ q)
if test output == "slippy: command line: invalid command" -a $? -eq 1
then
    echo 'Test 1c Passed'
else
    echo 'Test 1c Failed'
fi

#       d: Letters instead of num
output=$(./slippy lvq)
if test output == "slippy: command line: invalid command" -a $? -eq 1
then
    echo 'Test 1d Passed'
else
    echo 'Test 1d Failed'
fi

#     e: Negative Line num
output=$(./slippy -1q)
if test output == "usage: slippy [-i] [-n] [-f <script-file> | <sed-command>] [<files>...]" -a $? -eq 1
then
    echo 'Test 1e Passed'
else
    echo 'Test 1e Failed'
fi

# Test 2: Line Number 
#       a: 0 num
output=$(./slippy 0q)
if test output == "slippy: command line: invalid command" -a $? -eq 1
then
    echo 'Test 2a Passed'
else
    echo 'Test 2a Failed'

#       b: Start
output=$(echo lol\n idk | ./slippy 1q)
if test output == "lol" -a $? -eq 0
then
    echo 'Test 2b Passed'
else
    echo 'Test 2b Failed'

#       c: middle
output=$(seq 50 75 | ./slippy 3q)
if test output == "50\n51\n52\n" -a $? -eq 0
then
    echo 'Test 2c Passed'
else
    echo 'Test 2c Failed'

#       d: End 
output=$(echo lol\nidk | ./slippy 2q)
if test output == "lol\nidk" -a $? -eq 0
then
    echo 'Test 2d Passed'
else
    echo 'Test 2d Failed'
