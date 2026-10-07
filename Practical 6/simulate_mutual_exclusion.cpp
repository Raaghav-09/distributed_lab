#include <iostream>
#include <vector>
#include <queue>
#include <algorithm>

using namespace std;

struct Request {
    int timestamp;
    int processId;
};

class Process {
private:
    int id;
    int clock;
    bool requesting;
    bool inCriticalSection;
    int repliesReceived;
    vector<bool> deferred;
    queue<Request> requestQueue;

public:
    Process(int processId, int totalProcesses)
        : id(processId), clock(0), requesting(false),
          inCriticalSection(false), repliesReceived(0),
          deferred(totalProcesses, false) {
    }

    int getId() const {
        return id;
    }

    int getClock() const {
        return clock;
    }

    void incrementClock() {
        clock++;
    }

    void requestCriticalSection(vector<Process>& processes) {
        requesting = true;
        inCriticalSection = false;
        repliesReceived = 0;
        incrementClock();

        Request request = {clock, id};

        cout << "\nProcess " << id
             << " requests Critical Section at timestamp "
             << clock << endl;

        for (Process& process : processes) {
            if (process.getId() != id) {
                sendRequest(process, request);
            }
        }
    }

    void sendRequest(Process& receiver, Request request) {
        receiver.receiveRequest(request, *this);
    }

    void receiveRequest(Request request, Process& sender) {
        clock = max(clock, request.timestamp) + 1;

        bool sendReply = false;

        if (!requesting && !inCriticalSection) {
            sendReply = true;
        } else if (inCriticalSection) {
            sendReply = false;
        } else {
            Request ownRequest = {clock, id};

            if (request.timestamp < ownRequest.timestamp ||
                (request.timestamp == ownRequest.timestamp &&
                 request.processId < ownRequest.processId)) {
                sendReply = true;
            } else {
                deferred[sender.getId()] = true;
            }
        }

        if (sendReply) {
            sender.receiveReply();
        } else {
            requestQueue.push(request);
        }
    }

    void receiveReply() {
        repliesReceived++;
    }

    bool canEnterCriticalSection(int totalProcesses) const {
        return requesting && repliesReceived == totalProcesses - 1;
    }

    void enterCriticalSection() {
        requesting = false;
        inCriticalSection = true;

        cout << "Process " << id
             << " enters the Critical Section." << endl;

        cout << "Process " << id
             << " is executing the Critical Section." << endl;
    }

    void leaveCriticalSection(vector<Process>& processes) {
        inCriticalSection = false;

        cout << "Process " << id
             << " leaves the Critical Section." << endl;

        for (Process& process : processes) {
            if (process.getId() != id && deferred[process.getId()]) {
                process.receiveReply();
                deferred[process.getId()] = false;
            }
        }

        repliesReceived = 0;
    }
};

int main() {
    const int NUM_PROCESSES = 3;

    vector<Process> processes;
    for (int i = 0; i < NUM_PROCESSES; i++) {
        processes.emplace_back(i, NUM_PROCESSES);
    }

    cout << "Distributed Mutual Exclusion Simulation" << endl;
    cout << "Using Ricart-Agrawala Algorithm" << endl;

    processes[0].requestCriticalSection(processes);

    if (processes[0].canEnterCriticalSection(NUM_PROCESSES)) {
        processes[0].enterCriticalSection();
        processes[0].leaveCriticalSection(processes);
    }

    processes[1].requestCriticalSection(processes);

    if (processes[1].canEnterCriticalSection(NUM_PROCESSES)) {
        processes[1].enterCriticalSection();
        processes[1].leaveCriticalSection(processes);
    }

    processes[2].requestCriticalSection(processes);

    if (processes[2].canEnterCriticalSection(NUM_PROCESSES)) {
        processes[2].enterCriticalSection();
        processes[2].leaveCriticalSection(processes);
    }

    cout << "\nSimulation completed successfully." << endl;

    return 0;
}
