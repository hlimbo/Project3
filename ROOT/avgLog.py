import sys

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Name of log file to process required!")
    else:
        with open(sys.argv[1]) as log:
            avg = 0.0
            count = 0.0
            for line in log:
                avg+=float(line)
                count+=1
            if count > 0:
                print("Average of all recorded lines: "+str(avg/count))
            else:
                print("Log file was empty")
