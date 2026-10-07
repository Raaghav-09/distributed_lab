#include <iostream>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/wait.h>
#include <unistd.h>

using namespace std;

int main() {
    key_t key = ftok("shmfile", 65);

    int shmid = shmget(key, sizeof(int), 0666 | IPC_CREAT);
    if (shmid == -1) {
        cerr << "Shared memory creation failed." << endl;
        return 1;
    }

    int *counter = (int *)shmat(shmid, nullptr, 0);
    if (counter == (int *)-1) {
        cerr << "Shared memory attachment failed." << endl;
        return 1;
    }

    *counter = 0;

    const int NUM_PROCESSES = 2;
    const int INCREMENTS = 100000;

    for (int i = 0; i < NUM_PROCESSES; i++) {
        pid_t pid = fork();

        if (pid == -1) {
            cerr << "Fork failed." << endl;
            return 1;
        }

        if (pid == 0) {
            for (int j = 0; j < INCREMENTS; j++) {
                (*counter)++;
            }

            shmdt(counter);
            return 0;
        }
    }

    for (int i = 0; i < NUM_PROCESSES; i++) {
        wait(nullptr);
    }

    cout << "Final counter value: " << *counter << endl;
    cout << "Expected counter value: "
         << NUM_PROCESSES * INCREMENTS << endl;

    shmdt(counter);
    shmctl(shmid, IPC_RMID, nullptr);

    return 0;
}
