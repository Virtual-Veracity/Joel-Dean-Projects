
import re

if __name__ == "__main__":

    message = "^" + "hans" + "\s" + re.escape("falcon*solo") + "$"
    regex = re.compile(message, re.ASCII)
    print(regex)

    for line in open("credentials.txt"):
        match = regex.search(line)
        if match != None:
            print(match.group(0))