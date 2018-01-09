// Compile with
// $ g++ -Wall sos_log_who.cpp -o sos_log_who
#define HOST_NAME_MAX 255
#define LOGIN_NAME_MAX 32
#define TIME_BUFFER_SIZE 80
#define MICROSECONDS 1000 * 1000

#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <time.h>
#include <iostream>

void printCharArray(FILE *pFile, char array[]) {
    for(int i = 0; i < strlen(array); i++) {
        fprintf(pFile, "%c", array[i]);
    }
}

int main() {
    std::cout << "PID " << ::getpid() << " (parent-PID: " << ::getppid() << ")" << std::endl;
    std::cout << "ttttt" << std::endl;

    FILE *pFile;
    pFile = fopen("sos_log_who.log", "a");

    // Loop until process is killed
    for(;;) {

        time_t rawtime;
        struct tm * timeinfo;
        char buffer[TIME_BUFFER_SIZE];

        time (&rawtime);
        timeinfo = localtime (&rawtime);
        // http://www.cplusplus.com/reference/ctime/strftime/
        strftime (buffer, TIME_BUFFER_SIZE, "%c :: ", timeinfo);
        printCharArray(pFile, buffer);

        char hostname[HOST_NAME_MAX];
        gethostname(hostname, HOST_NAME_MAX);
        printCharArray(pFile, hostname);

        fprintf(pFile, "%s", " - ");

        // FIXME - get full lists of logged users
        char username[LOGIN_NAME_MAX];
        getlogin_r(username, LOGIN_NAME_MAX);
        printCharArray(pFile, username);

        fprintf(pFile, "%s", "\n");

        fflush (pFile);
        usleep(30 * MICROSECONDS);
    }

    return 0;
}
