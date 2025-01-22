#! /bin/dash
# ==================    COMP 2041  ====================
#                     Written By - z5208947
# Tests are assumed to run in a the proper directory

# ===============================    Tests for subset 0   ===================================
#
rm -rf temp_test 2> /dev/null
mkdir temp_test
cd temp_test
#
#  ---------   tigger-init   ------------
echo "Beginning tigger-init Testing"

# Tests
# Test 1: Invalid Argument Check
# Test 2: No repository 
# Test 3: Repository already constructed

# Test 1: Invalid Argument Check
output=$(../tigger-init More Arguments 2>&1)
if test "$output" = 'usage: tigger-init' -a "$?" -eq 1
then 
    echo "Test 1 Passed"
else 
    echo "Test 1 Failed"
fi

rm -rf .* 2> /dev/null

# Test 2: No repository
output=$(../tigger-init)
if test -d .tigger -a "$output" = 'Initialized empty tigger repository in .tigger' -a "$?" -eq 0
then 
    echo "Test 2 Passed"
else 
    echo "Test 2 Failed"
fi 


rm -rf .* 2> /dev/null


# Test 3: Repository already constructed
mkdir .tigger
output=$(../tigger-init 2>&1)
if test "$output" = 'tigger-init: error: .tigger already exists' -a "$?" -eq 1
then 
    echo "Test 3 Passed"
else 
    echo "Test 3 Failed"
fi 

rm -rf .* 2> /dev/null

echo "Clear temp_test folder\n"

#  ---------   tigger-add   ------------

echo "Beginning tigger-add Testing"

# Tests
# Test 1: Invalid Argument Check
# Test 2: No repository 
# Test 3: Invalid filename
# Test 4: File Does NOT exist
# Test 5: Add Files to Index

# Test 1: Invalid Argument Check
../tigger-init > /dev/null
output=$(../tigger-add  2>&1)
if test "$output" = 'usage: tigger-add <filenames>' -a "$?" -eq 1
then 
    echo "Test 1 Passed"
else 
    echo "Test 1 Failed"
fi 

rm -rf .* 2> /dev/null

# Test 2: No repository
touch sample.txt
echo '123' > sample.txt 
output=$(../tigger-add sample.txt 2>&1)
if test "$output" = 'tigger-add: error: tigger repository directory .tigger not found' -a "$?" -eq 1
then 
    echo "Test 2 Passed"
else
    echo "Test 2 Failed"
fi 

# Test 3: Invalid filename
../tigger-init > /dev/null
output=$(../tigger-add .cantstartwith 2>&1)
if test "$output" = "tigger-add: error: invalid filename '.cantstartwith'" -a $? -eq 1
then
    echo 'Test 3a Passed'
else 
    echo 'Test 3a Failed'
fi 
output=$(../tigger-add '_cantstartwith' 2>&1)
if test "$output" = "tigger-add: error: invalid filename '_cantstartwith'" -a $? -eq 1
then
    echo 'Test 3b Passed'
else 
    echo 'Test 3b Failed'
fi 
output=$(../tigger-add 'Nonalphanumeric%' 2>&1)
if test "$output" = "tigger-add: error: invalid filename 'Nonalphanumeric%'" -a $? -eq 1
then
    echo 'Test 3c Passed'
else 
    echo 'Test 3c Failed'
fi 

rm -rf .* 2> /dev/null

# Test 4: File Does NOT exist
../tigger-init > /dev/null
output=$(../tigger-add toothfairy.jpg 2>&1)
if test "$output" = "tigger-add: error: can not open 'toothfairy.jpg'" -a $? -eq 1
then 
    echo 'Test 4 Passed'
else
    echo 'Test 4 Failed'
fi 

rm -rf .* 2> /dev/null

# Test 5: Add Files to Index
# Test 5a: One File
../tigger-init > /dev/null
touch test_add
echo 123 > test_add
output=$(../tigger-add test_add)
if test -e '.tigger/master/index/test_add' -a $? -eq 0 -a "$(cat '.tigger/master/index/test_add')" = '123'
then  
    echo 'Test 5a Passed'
else 
    echo 'Test 5a Failed'
fi 

rm -rf .* 2> /dev/null

# Test 5b: Multiple Files
../tigger-init > /dev/null
touch test_add test_multiple
echo '123' > test_add
echo 'abc' > test_multiple
output=$(../tigger-add test_add test_multiple)
if test -e '.tigger/master/index/test_add' -a $? -eq 0 -a "$(cat '.tigger/master/index/test_add')" = '123'
then  
    if test -e '.tigger/master/index/test_multiple' -a $? -eq 0 -a "$(cat '.tigger/master/index/test_multiple')" = 'abc'
    then
        echo 'Test 5b Passed'
    else 
        echo 'Test 5b Failed'
    fi 
else 
    echo 'Test 5b Failed'
fi 

rm -rf .* 2> /dev/null

# Test 5c: Sequential add Commands
../tigger-init > /dev/null
touch test_add test_multiple
echo '123' > test_add
echo 'abc' > test_multiple
output=$(../tigger-add test_add)
output=$(../tigger-add test_multiple)
if test -e '.tigger/master/index/test_add' -a $? -eq 0 -a "$(cat '.tigger/master/index/test_add')" = '123'
then  
    if test -e '.tigger/master/index/test_multiple' -a $? -eq 0 -a "$(cat '.tigger/master/index/test_multiple')" = 'abc'
    then
        echo 'Test 5c Passed'
    else 
        echo 'Test 5c Failed'
    fi 
else 
    echo 'Test 5b Failed'
fi 

rm -rf .* 2> /dev/null
echo "Clear temp_test folder\n"

#  ---------   tigger-commit   ------------

echo "Beginning tigger-commit Testing"

# Tests
# Test 1: No repository 
# Test 2: Invalid Argument Check
# Test 3: Nothing to Commmit (Empty Index + Unchanged Index)
# Test 4: Valid Message
# Test 5: Create Commit with message

# Test 1: No repository
output=$(../tigger-commit 2>&1)
if test "$output" = 'tigger-commit: error: tigger repository directory .tigger not found' -a "$?" -eq 1
then 
    echo "Test 1 Passed"
else
    echo "Test 1 Failed"
fi 

rm -rf .* 2> /dev/null

# Test 2: Invalid Argument Check
../tigger-init > /dev/null
output=$(../tigger-commit  2>&1)
if test "$output" = 'usage: tigger-commit [-a] -m commit-message' -a "$?" -eq 1
then 
    echo "Test 2a Passed"
else 
    echo "Test 2a Failed"
fi 

output=$(../tigger-commit -p Is a proper message  2>&1)
if test "$output" = 'usage: tigger-commit [-a] -m commit-message' -a "$?" -eq 1
then 
    echo "Test 2b Passed"
else 
    echo "Test 2b Failed"
fi 

output=$(../tigger-commit -m 2>&1)
if test "$output" = 'usage: tigger-commit [-a] -m commit-message' -a "$?" -eq 1
then 
    echo "Test 2c Passed"
else 
    echo "Test 2c Failed"
fi 

output=$(../tigger-commit -m -wrong 2>&1)
if test "$output" = 'usage: tigger-commit [-a] -m commit-message' -a "$?" -eq 1
then 
    echo "Test 2d Passed"
else 
    echo "Test 2d Failed"
fi 

rm -rf .* 2> /dev/null

# Test 3: Nothing to Commmit (Empty Index + Unchanged Index)
../tigger-init > /dev/null
output=$(../tigger-commit -m "valid message")
if test "$output" = 'nothing to commit' -a "$?" -eq 0
then 
    echo "Test 3 Passed"
else 
    echo "Test 3 Failed"
fi 

rm -rf .* 2> /dev/null

# Test 4: Valid Commit
../tigger-init > /dev/null
echo 123 > test_file
../tigger-add test_file
output=$(../tigger-commit -m "valid message")
if test "$output" = 'Committed as commit 0' -a "$?" -eq 0
then 
    echo "Test 4 Passed"
else 
    echo "Test 4 Failed"
fi 

rm -rf .* 2> /dev/null

# Test 5: Create Commits with message
../tigger-init > /dev/null
echo 123 > test_file
../tigger-add test_file
output=$(../tigger-commit -m "valid message")
if test "$output" = 'Committed as commit 0' -a "$?" -eq 0 -a "$(cat .tigger/master/commits/commit0/commitMessage.txt)" = 'valid message'
then 
    echo "Test 5a Passed"
else 
    echo "Test 5a Failed"
fi 

echo abc > test_multiple
../tigger-add test_multiple
output=$(../tigger-commit -m 'second commit')
if test "$output" = 'Committed as commit 1' -a "$?" -eq 0 -a "$(cat .tigger/master/commits/commit1/commitMessage.txt)" = 'second commit'
then 
    echo "Test 5b Passed"
else 
    echo "Test 5b Failed"
fi 

rm -rf .* 2> /dev/null

# Test 6: commit -> nothing to commit -> commit
../tigger-init > /dev/null
echo 123 > test_file
../tigger-add test_file
output=$(../tigger-commit -m "first commit")
if test "$output" = 'Committed as commit 0' -a "$?" -eq 0
then 
    echo "Test 6a Passed"
else 
    echo "Test 6a Failed"
fi 

output=$(../tigger-commit -m "valid message")
if test "$output" = 'nothing to commit' -a "$?" -eq 0
then 
    echo "Test 6b Passed"
else 
    echo "Test 6b Failed"
fi 

echo abc >> test_file
../tigger-add test_file
output=$(../tigger-commit -m "second commit")
if test "$output" = 'Committed as commit 1' -a "$?" -eq 0
then 
    echo "Test 6c Passed"
else 
    echo "Test 6c Failed"
fi 

rm -rf .* 2> /dev/null
echo "Clear temp_test folder\n"

#  ---------   tigger-log   ------------

echo "Beginning tigger-log Testing"

# Tests
# Test 1: No repository 
# Test 2: Invalid Argument Check
# Test 3: No Previous Commits
# Test 4: Working log
# Test 5: Multiple logs

# Test 1: No repository 
output=$(../tigger-log 2>&1)
if test "$output" = 'tigger-log: error: tigger repository directory .tigger not found' -a "$?" -eq 1
then
    echo "Test 1 Passed"
else 
    echo "Test 1 Failed"
fi

rm -rf .* 2> /dev/null

# Test 2: Invalid Argument Check
../tigger-init > /dev/null
output=$(../tigger-log notuseful 2>&1)
if test "$output" = 'usage: tigger-log' -a "$?" -eq 1
then
    echo "Test 2 Passed"
else 
    echo "Test 2 Failed"
fi

rm -rf .* 2> /dev/null

# Test 3: No Previous Commits
../tigger-init > /dev/null
output=$(../tigger-log)
if test "$output" = '' -a "$?" -eq 0
then
    echo "Test 3 Passed"
else 
    echo "Test 3 Failed"
fi

rm -rf .* 2> /dev/null

# Test 4: Working log
../tigger-init > /dev/null
output=$(../tigger-log)
if test "$output" = '' -a "$?" -eq 0
then
    echo "Test 4 Passed"
else 
    echo "Test 4 Failed"
fi

rm -rf .* 2> /dev/null

# Test 5: Multiple logs
../tigger-init > /dev/null
echo 123 > test_file
../tigger-add test_file
../tigger-commit -m first > /dev/null
echo abc > test_multiple
../tigger-add test_multiple
../tigger-commit -m second > /dev/null
../tigger-log > check_file
echo '1 second' > base_file
echo '0 first' >> base_file
if test -z $(diff -q check_file base_file)
then
    echo "Test 5 Passed"
else 
    echo "Test 5 Failed"
fi

rm -rf .* 2> /dev/null
echo "Clear temp_test folder\n"

#  ---------   tigger-show   ------------

echo "Beginning tigger-show Testing"

# Tests
# Test 1: No repository 
# Test 2: Invalid Argument Check
# Test 3: Invalid Filename
# Test 4: Empty Index / File Doesn't Exist
# Test 5: Working FileName Only
# Test 7: Valid Commit/File

# Test 1: No repository 
output=$(../tigger-show 2>&1)
if test "$output" = 'tigger-show: error: tigger repository directory .tigger not found' -a "$?" -eq 1
then
    echo "Test 1 Passed"
else 
    echo "Test 1 Failed"
fi

rm -rf .* 2> /dev/null

# Test 2: Invalid Argument Check
../tigger-init > /dev/null
output=$(../tigger-show 2>&1)
if test "$output" = 'usage: tigger-show <commit>:<filename>' -a "$?" -eq 1
then
    echo "Test 2a Passed"
else 
    echo "Test 2a Failed"
fi

output=$(../tigger-show Arg1 Arg2 2>&1)
if test "$output" = 'usage: tigger-show <commit>:<filename>' -a "$?" -eq 1
then
    echo "Test 2b Passed"
else 
    echo "Test 2b Failed"
fi

rm -rf .* 2> /dev/null

# Test 3: Invalid Object
../tigger-init > /dev/null
output=$(../tigger-show something 2>&1)
if test "$output" = "tigger-show: error: invalid object something" -a "$?" -eq 1
then
    echo "Test 3 Passed"
else 
    echo "Test 3 Failed"
fi

rm -rf .* 2> /dev/null

# Test 4: Invalid FileName
../tigger-init > /dev/null
output=$(../tigger-show :nope 2>&1)
if test "$output" = "tigger-show: error: invalid filename 'nope'" -a "$?" -eq 1
then
    echo "Test 4a Passed"
else 
    echo "Test 4a Failed"
fi

output=$(../tigger-show : 2>&1)
if test "$output" = "tigger-show: error: invalid filename ''" -a "$?" -eq 1
then
    echo "Test 4b Passed"
else 
    echo "Test 4b Failed"
fi

output=$(../tigger-show :_notarealfile 2>&1)
if test "$output" = "tigger-show: error: invalid filename '_notarealfile'" -a "$?" -eq 1
then
    echo "Test 4c Passed"
else 
    echo "Test 4c Failed"
fi

rm -rf .* 2> /dev/null

# Test 5: Filename Only -> Not in index
../tigger-init > /dev/null
output=$(../tigger-show :test_file 2>&1)
if test "$output" = "tigger-show: error: 'test_file' not found in index" -a "$?" -eq 1
then
    echo "Test 5 Passed"
else 
    echo "Test 5 Failed"
fi

rm -rf .* 2> /dev/null

# Test 6: Working FileName Only
../tigger-init > /dev/null
echo 123 > test_file
../tigger-add test_file
output=$(../tigger-show :test_file 2>&1)
if test "$output" = "123" -a "$?" -eq 0
then
    echo "Test 6 Passed"
else 
    echo "Test 6 Failed"
fi

rm -rf .* 2> /dev/null

# Test 7: Invalid Commit
../tigger-init > /dev/null
echo 123 > test_file
../tigger-add test_file
output=$(../tigger-show 0:test_file 2>&1)
if test "$output" = "tigger-show: error: unknown commit '0'" -a "$?" -eq 1
then
    echo "Test 7 Passed"
else 
    echo "Test 7 Failed"
fi

rm -rf .* 2> /dev/null

# Test 8: Valid Commit/ Invalid File
../tigger-init > /dev/null
echo 123 > test_file
../tigger-add test_file
../tigger-commit -m 'testMessage' > /dev/null
output=$(../tigger-show 0:find_me 2>&1)
if test "$output" = "tigger-show: error: 'find_me' not found in commit 0" -a "$?" -eq 1
then
    echo "Test 8 Passed"
else 
    echo "Test 8 Failed"
fi

rm -rf .* 2> /dev/null

# Test 9: Valid Commit / Valid File
../tigger-init > /dev/null
echo 123 > test_file
../tigger-add test_file
../tigger-commit -m 'testMessage' > /dev/null
output=$(../tigger-show 0:test_file)
if test "$output" = "123" -a "$?" -eq 0
then
    echo "Test 9 Passed"
else 
    echo "Test 9 Failed"
fi


rm -rf .* 2> /dev/null
echo "Clear temp_test folder\n"



