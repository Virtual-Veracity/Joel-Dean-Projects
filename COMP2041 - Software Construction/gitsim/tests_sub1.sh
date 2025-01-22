#! /bin/dash

# Tests are assumed to run in a the proper directory

# ===============================    Tests for Subset 1   ===================================
#
rm -rf temp_test 2> /dev/null
mkdir temp_test
cd temp_test
#
#  ---------   tigger-commit [-a]   ------------
echo "Beginning tigger-commit [-a] Testing"

# Tests
# Test 1: Invalid Argument Check
# Test 2: [-a] Option Works

# Test 1: Invalid Argument Check
../tigger-init > /dev/null
echo '123' > test_file
../tigger-add test_file
output=$(../tigger-commit no -m message 2>&1)
if test "$output" = 'usage: tigger-commit [-a] -m commit-message' -a "$?" -eq 1
then 
    echo 'Test 1 Passed'
else 
    echo 'Test 1 Failed'
fi 

rm -rf * 2> /dev/null

# Test 2: [-a] Option Works
../tigger-init > /dev/null
echo '123' > test_file
../tigger-add test_file 
echo 'abc' > test_file
output=$(../tigger-commit -a -m message)
if test "$output" = 'Committed as commit 0' -a "$?" -eq 0 -a "$(cat .tigger/master/commits/commit0/test_file)" = 'abc'
then 
    echo 'Test 2 Passed'
else 
    echo 'Test 2 Failed'
fi 

rm -rf * 2> /dev/null
echo 'Cleared temp_test Folder'

#  ---------   tigger-rm ------------

echo "\nBeginning tigger-rm Testing"

# Tests
# Test 1: No repository 
# Test 2: Invalid Argument Check
    # 2a: Just command with no Files
    # 2b: Only options
    # 2c: Option occurs within filenames 

# Test 3: Invalid Filename
    # 3a: Each filename is completely (commanded) before next filename

# Test 4: --cached option
    # 4a: Option at start of filename list works 'correctly'
    # 4b: Option at end of filename list works 'correctly'
    # 4c: Error staged occurs with cached (No error)
    # 4d; Error diff occurs with cached (No error)
    # 4e: Both Errors occur with cached (Yes Error)

# Test 5: --force option
    # Ordinary remove with force
    # Stage error with force
    # Diff Error with force
    # Both errors with force

# Test 6: Error (Changes Staged)
    # Catch a staged changes error
    # catch a diff working/index 
    # Catch Both when occuring simultaneously

# Test 1: No repository 
output=$(../tigger-rm test_file 2>&1)
if test "$output" = 'tigger-rm: error: tigger repository directory .tigger not found' -a "$?" -eq 1
then 
    echo 'Test 1 Passed'
else 
    echo 'Test 1 Failed'
fi 

rm -rf * 2> /dev/null

# Test 2: Invalid Argument Check
    # 2a: Just command with no Files
../tigger-init > /dev/null
output=$(../tigger-rm 2>&1)
if test "$output" = 'usage: tigger-rm [--force] [--cached] <filenames>' -a "$?" -eq 1
then 
    echo 'Test 2a Passed'
else 
    echo 'Test 2a Failed'
fi 

# 2b: Only options
output=$(../tigger-rm --cached 2>&1)
if test "$output" = 'usage: tigger-rm [--force] [--cached] <filenames>' -a "$?" -eq 1
then 
    echo 'Test 2bi Passed'
else 
    echo 'Test 2bi Failed'
fi 
output=$(../tigger-rm --cached --force 2>&1)
if test "$output" = 'usage: tigger-rm [--force] [--cached] <filenames>' -a "$?" -eq 1
then 
    echo 'Test 2bii Passed'
else 
    echo 'Test 2bii Failed'
fi 

# 2c: Option occurs within filenames 
touch test_file renny.txt
output=$(../tigger-rm --cached renny.txt --force test_file 2>&1)
if test "$output" = 'usage: tigger-rm [--force] [--cached] <filenames>' -a "$?" -eq 1
then 
    echo 'Test 2c Passed'
else 
    echo 'Test 2c Failed'
fi 

rm -rf * 2> /dev/null

# Test 3: Invalid Filename
    # 3a: Each filename is completely (commanded) before next filename
../tigger-init > /dev/null
touch test_file 
output=$(../tigger-rm .badFile 2>&1)
if test "$output" = "tigger-rm: error: invalid filename '.badFile'" -a "$?" -eq 1
then 
    echo 'Test 3a Passed'
else 
    echo 'Test 3a Failed'
fi 

echo 123 > test_file 
../tigger-add test_file
../tigger-commit -m "Status Quo" > /dev/null
output=$(../tigger-rm test_file bad%File 2>&1)
if test "$output" = "tigger-rm: error: invalid filename 'bad%File'" -a "$?" -eq 1
then 
    echo 'Test 3b Passed'
else 
    echo 'Test 3b Failed'
fi 

rm -rf * 2> /dev/null


# Test 4: --cached option
    # 4a: Option at start of filename list works 'correctly'
    ../tigger-init > /dev/null
    echo 123 > test_file 
    ../tigger-add test_file
    ../tigger-commit -m "Status Quo" > /dev/null
    output=$(../tigger-rm --cached test_file)
    if test -e './test_file' -a "$?" -eq 0
    then 
        if test ! -e '.tigger/master/index/test_file'
        then 
            echo 'Test 4a Passed'
        else 
            echo 'Test 4a Failed'
        fi
    else 
        echo 'Test 4a Failed'
    fi 

    # 4b: Option at end of filename list works 'correctly'
    ../tigger-add test_file
    output=$(../tigger-rm test_file --cached)
    if test -e './test_file' -a "$?" -eq 0
    then 
        if test ! -e '.tigger/master/index/test_file'
        then 
            echo 'Test 4b Passed'
        else 
            echo 'Test 4b Failed'
        fi 
    else 
        echo 'Test 4b Failed'
    fi 

    # 4c: Error staged occurs with cached (No error)
    echo abc >> test_file
    ../tigger-add test_file
    output=$(../tigger-rm test_file --cached)
    if test ! -e '.tigger/master/index/test_file' -a "$?" -eq 0
    then 
        echo 'Test 4c Passed'
    else 
        echo 'Test 4c Failed'
    fi 

    # 4d: Error diff occurs with cached (No error)
    echo abc > test_file
    ../tigger-add test_file
    ../tigger-commit -m "same in index as commit" > /dev/null
    echo 123 > test_file
    output=$(../tigger-rm test_file --cached)
    if test ! -e '.tigger/master/index/test_file' -a "$?" -eq 0
    then 
        echo 'Test 4d Passed'
    else 
        echo 'Test 4d Failed'
    fi 

    # 4e: Both Errors occur with cached (Yes Error)
    echo zyx > test_file
    ../tigger-add test_file
    ../tigger-commit -m "Double Error" > /dev/null
    echo 123 > test_file
    ../tigger-add test_file
    echo abc > test_file 
    output=$(../tigger-rm test_file --cached)
    if test -e '.tigger/master/index/test_file' -a "$?" -eq 1 -a "$output" = "tigger-rm: error: 'test_file' in index is different to both the working file and the repository"
    then 
        echo 'Test 4e Passed'
    else 
        echo 'Test 4e Failed'
    fi 

rm -rf * 2> /dev/null

# Test 5: --force option
    # Ordinary remove with force
    # 5a: Option at start of filename list works 'correctly'
    ../tigger-init > /dev/null
    echo 123 > test_file
    ../tigger-add test_file
    ../tigger-commit -m "not Diff" > /dev/null
    output=$(../tigger-rm --force test_file)
    if test ! -e './test_file' -a "$?" -eq 0
    then 
        if test ! -e '.tigger/master/index/test_file'
        then 
            echo 'Test 5a Passed'
        else 
            echo 'Test 5a Failed'
        fi 
    else 
        echo 'Test 5a Failed'
    fi 

    # Stage error with force
    echo abc > test_file
    ../tigger-add test_file
    output=$(../tigger-rm test_file --force)
    if test ! -e '.tigger/master/index/test_file' -a "$?" -eq 0
    then 
        if test ! -e './test_file'
        then
            echo 'Test 5b Passed'
        fi
    else 
        echo 'Test 5b Failed'
    fi 

    # Diff Error with force
    echo 123 > test_file
    ../tigger-add test_file
    ../tigger-commit -m 'message' > /dev/null
    echo abc > test_file
    output=$(../tigger-rm --force test_file)
    if test ! -e '.tigger/master/index/test_file' -a "$?" -eq 0
    then 
        if test ! -e './test_file'
        then
            echo 'Test 5c Passed'
        else 
            echo 'Test 5c Failed'
        fi 
    else 
        echo 'Test 5c Failed'
    fi 

    # Both errors with force
    echo 123 > test_file
    ../tigger-add test_file
    ../tigger-commit -m 'message' > /dev/null
    echo abc > test_file 
    ../tigger-add test_file 
    echo 1a2b3c > test_file
    output=$(../tigger-rm --force test_file)
    if test ! -e '.tigger/master/index/test_file' -a "$?" -eq 0
    then 
        if test ! -e './test_file'
        then
            echo 'Test 5d Passed'
        else 
            echo 'Test 5d Failed'
        fi 
    else 
        echo 'Test 5d Failed'
    fi 

rm -rf * 2> /dev/null

# Test 6: Error (Changes Staged)
    # Catch a staged changes error
    ../tigger-init > /dev/null
    echo abc > test_file
    ../tigger-add test_file
    output=$(../tigger-rm test_file)
    if test "$output" = "tigger-rm: error: 'test_file' has staged changes in the index" -a "$?" -eq 1
    then 
        echo 'Test 6a Passed'
    else 
        echo 'Test 6a Failed'
    fi 

    # catch a diff working/index 
    echo 123 > test_file
    ../tigger-add test_file
    ../tigger-commit -m 'message' > /dev/null
    echo abc > test_file
    output=$(../tigger-rm test_file)
    if test "$output" = "tigger-rm: error: 'test_file' in the repository is different to the working file" -a "$?" -eq 1
    then 
        echo 'Test 6b Passed'
    else 
        echo 'Test 6b Failed'
    fi 

    # Catch Both when occuring simultaneously
    echo 123 > test_file
    ../tigger-add test_file
    ../tigger-commit -m 'message' > /dev/null
    echo abc > test_file 
    ../tigger-add test_file 
    echo 1a2b3c > test_file
    output=$(../tigger-rm test_file)
    if test "$output" = "tigger-rm: error: 'test_file' in index is different to both the working file and the repository" -a "$?" -eq 1
    then 
        echo 'Test 6c Passed'
    else 
        echo 'Test 6c Failed'
    fi 

rm -rf * 2> /dev/null
echo 'Cleared temp_test Folder'

ls

#  ---------   tigger-status ------------
echo "\nBeginning tigger-status Testing"

# Tests
# Test 1: No repository
# Test 2: Invalid Argument Check - Not Needed
# Test 3: No Files
# Test 4: Untracked
# Test 5: deleted
# Test 6: file deleted
# Test 7: added to index
# Test 8: same as repo
# Test 9: file changed, changes not staged for commit
# Test 10: file changed, chnages staged for commit
# Test 11: file changed, different changes staged for commit
# Test 12: Multiple Files Displayed Correctly


# Test 1: No repository 
output=$(../tigger-status 2>&1)
if test "$output" = 'tigger-status: error: tigger repository directory .tigger not found' -a "$?" -eq 1
then 
    echo 'Test 1 Passed'
else 
    echo 'Test 1 Failed'
fi 

rm -rf * 2> /dev/null

# Test 2: Invalid Argument Check
# Not Needed
echo 'Test 2 Passed'

# Test 3: No Files
../tigger-init > /dev/null
output=$(../tigger-status)
output=$(echo $output | grep -v 'tigger')
echo $output
if test "$output" = '' -a "$?" -eq 0
then 
    echo 'Test 3 Passed'
else 
    echo 'Test 3 Failed'
fi 

rm -rf * 2> /dev/null

# Test 4: Untracked
../tigger-init > /dev/null
echo 123 > test_file
output=$(../tigger-status)
output=$(echo $output | grep -v 'tigger')
if test "$output" = 'test_file - untracked' -a "$?" -eq 0
then 
    echo 'Test 4 Passed'
else 
    echo 'Test 4 Failed'
fi 

rm -rf * 2> /dev/null

# Test 5: deleted
../tigger-init > /dev/null
echo 123 > test_file
../tigger-add test_file 
../tigger-commit -m "message" > /dev/null
../tigger-rm test_file
output=$(../tigger-status)
output=$(echo $output | grep -v 'tigger')
if test "$output" = 'test_file - deleted' -a "$?" -eq 0
then 
    echo 'Test 5 Passed'
else 
    echo 'Test 5 Failed'
fi 

rm -rf * 2> /dev/null

# Test 6: file deleted
../tigger-init > /dev/null
echo 123 > test_file
../tigger-add test_file 
../tigger-commit -m "message" > /dev/null
rm ../test_file
output=$(../tigger-status)
output=$(echo $output | grep -v 'tigger')
if test "$output" = 'test_file - file deleted' -a "$?" -eq 0
then 
    echo 'Test 6 Passed'
else 
    echo 'Test 6 Failed'
fi 

rm -rf * 2> /dev/null

# Test 7: added to index / file deleted
echo 123 > test_file
../tigger-add test_file 
output=$(../tigger-status)
output=$(echo $output | grep -v 'tigger')
if test "$output" = 'test_file - added to index' -a "$?" -eq 0
then 
    echo 'Test 7a Passed'
else 
    echo 'Test 7a Failed'
fi 

rm -rf * 2> /dev/null

echo 123 > test_file
../tigger-add test_file 
rm ../test_file
output=$(../tigger-status)
output=$(echo $output | grep -v 'tigger')
if test "$output" = 'test_file - added to index, file deleted' -a "$?" -eq 0
then 
    echo 'Test 7b Passed'
else 
    echo 'Test 7b Failed'
fi 

rm -rf * 2> /dev/null

# Test 8: same as repo
echo 123 > test_file
../tigger-add test_file 
../tigger-commit -m "message" > /dev/null
../tigger-rm test_file
output=$(../tigger-status)
output=$(echo $output | grep -v 'tigger')
if test "$output" = 'test_file - same as repo' -a "$?" -eq 0
then 
    echo 'Test 8 Passed'
else 
    echo 'Test 8 Failed'
fi 

rm -rf * 2> /dev/null

# Test 9: file changed, changes not staged for commit
echo 123 > test_file
../tigger-add test_file 
echo abc > test_file
output=$(../tigger-status)
output=$(echo $output | grep -v 'tigger')
if test "$output" = 'test_file - file changed, changes not staged for commit' -a "$?" -eq 0
then 
    echo 'Test 9 Passed'
else 
    echo 'Test 9 Failed'
fi 

# Test 10: file changed, changes staged for commit
echo 123 > test_file
../tigger-add test_file 
../tigger-commit -m "message" > /dev/null 
echo abc > test_file
../tigger-add test_file
output=$(../tigger-status)
output=$(echo $output | grep -v 'tigger')
if test "$output" = 'test_file - file changed, changes staged for commit' -a "$?" -eq 0
then 
    echo 'Test 10 Passed'
else 
    echo 'Test 10 Failed'
fi 

rm -rf * 2> /dev/null

# Test 11: file changed, different changes staged for commit
echo 123 > test_file
../tigger-add test_file 
../tigger-commit -m "message" > /dev/null 
echo abc > test_file
../tigger-add test_file
echo zyx . test_file
output=$(../tigger-status)
output=$(echo $output | grep -v 'tigger')
if test "$output" = 'test_file - file changed, different changes staged for commit' -a "$?" -eq 0
then 
    echo 'Test 11 Passed'
else 
    echo 'Test 11 Failed'
fi 


rm -rf * 2> /dev/null
echo 'Clear temp_test Folder'
