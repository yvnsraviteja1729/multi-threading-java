Sure! Here’s the translation of your multi-threading Java repository outline into English:

# Multi-threading in Java

This repository is based on "Java Multithreading, Concurrency & Performance Optimization" by Michael Pogrebinsky on Udemy.  

Period: 2023.11.20 ~ 2023.12.16

<details>
<summary>1. Introduction</summary>

### Motivation and OS Fundamentals

- Why do we need Threads?
    - Responsiveness (especially critical in User Interfaces) through concurrency = multitasking
    - Performance (processing more tasks in a shorter time) through parallelism
- Multithreading Caveats
    - Multithreaded programming fundamentally differs from single-threaded programming.

- What is a thread?
    - When you turn on a computer, the OS loads from disk to memory.
    - When we run an application, an instance is created in memory from disk.
    - This instance is known as a process or context of an application.
    - Processes operate independently from one another.
    - Composed of Files, Data (Heap), Code, and the Main Thread (Stack, Instruction Pointer).
    - In terms of threads, everything except the Stack and IP is shared.
    - The Stack is where local variables are stored, and the IP is the address of the next instruction to execute.

### OS Fundamentals Part 2

- What is a context switch?
    - Processes operate independently of each other.
    - A process can have one or more threads that compete for CPU time.
    - Hence, context switching is: Thread 1 executes -> Thread 1 pauses -> Thread 2 executes.
    - This is the price of multitasking.
    - If there are too many threads, trashing occurs, meaning more time is spent managing threads than doing actual work.
    - Switching between threads is cheaper than switching between processes.

- Thread Scheduling
    - First-Come, First-Serve: long-running threads can lead to starvation.
    - Shortest Job First: if short tasks keep arriving, longer ones may not get executed.
    - Actual OS implementations use dynamic priority (assigning priorities to User Interfaces, preventing starvation).

- Thread vs. Process
    - Thread:
        - If tasks share a lot of data
        - Faster
        - Switching is quicker
    - Process:
        - Higher importance for security and stability
        - Tasks are unrelated

</details>

<details>
<summary>2. Threading Fundamentals - Thread Creation</summary>

### Thread Creation 1

- The `Thread.sleep()` function instructs the OS not to schedule the current thread (it does not loop).
    - It does not occupy CPU time.

- You can set priority using `threadInstance.setPriority(1..10)`.

- You can register an exception handler using `thread.setUncaughtExceptionHandler()`.

### Thread Creation 2

- You can create threads by extending the `Thread` class and implementing the `run()` method.

</details>

<details>
<summary>3. Threading Fundamentals - Thread Coordination</summary>

### Thread Termination & Daemon Threads 1

[Thread Termination]
- Threads consume resources -> memory and kernel resources. If a thread uses the CPU, it also consumes CPU cycles and cache memory.
- When a thread finishes, its resources need to be cleaned up.
- If there are running threads, the application will not terminate.

[How to Interrupt]
- Add code to handle the interrupt signal.
- Execute methods that throw `InterruptedException`.

[Daemon Threads]
- These are threads that can terminate without affecting the application.
- They do not need to terminate gracefully.

### Joining Threads

[Why do we need it?]
- Other threads operate independently.
- Thread execution can be out of control.

[How?]
- A thread can loop and check if other threads have finished -> inefficient.
- A better approach is to let the thread sleep.
    - You can use `join()` for this.

</details>

<details>
<summary>4. Performance Optimization</summary>

[Performance in Multithreading]
- Latency - the time taken to complete a task, measured in time units.
- Throughput - the amount of tasks completed in a given period, measured in tasks per time unit.

[Latency]
1. If a task takes T time on a single core,
2. Dividing the task into N subtasks and executing them in parallel can reduce latency to T/N.

[N?]
- Generally, N should be the number of cores. If N exceeds the number of cores, it can backfire. The extra thread may lead to context switching, degrading cache performance and consuming additional memory.
- N = number of cores assumes no interruptions like IO blocking (no other processes occupying the CPU).
- Most computers support HyperThreading, which allows physical cores to run two threads simultaneously.

[Inherent Cost of Parallelization and Aggregation]
- Consider these costs:
    - Cost of dividing tasks
    - Cost of creating threads and assigning tasks
    - Time taken for thread startup and scheduling
    - Time for the last thread to finish and send signals
    - Time spent aggregating threads

- For tasks with short execution times, dividing the task might increase latency.

[Can we break any task into subtasks?]
- Not all tasks can be divided...
- Tasks can be made of independent code snippets that can be executed separately, tasks that must be executed sequentially, or a mix of both.

[Optimizing for Throughput]
- Throughput is defined as the number of tasks completed in a given time.
- Method 1: Breaking tasks into subtasks.
    - Latency = T/N
    - Throughput = N/T (improvement over 1/T).
    - However, it should be viewed as < N/T due to additional computational overheads (task division cost, thread creation and assignment cost, etc.).
- Method 2: Running tasks in parallel.
    - Assigning each task to different threads.
    - Throughput = N/T (this will be closer to N/T).

[Thread Pooling]
- Pre-create threads in a thread pool and use them as needed.

[QUIZ]
We are running an HTTP server on a single machine.  
Handling HTTP requests is delegated to a fixed-size pool of threads.  
Each request is handled by a single thread from the pool, which performs a blocking call to an external database that may take variable durations.  
After the database responds, the server thread sends an HTTP response to the user.  
Assuming we have a 64-core machine, what would be the optimal thread pool size to serve the HTTP requests?

-> Since blocking calls do not use CPU resources, we can allocate more threads than the number of cores (64) to optimize throughput.

</details>

<details>
<summary>5. Data Sharing Between Threads</summary>

[Stack?]
- A memory area where functions are called, parameters are passed, and local variables are stored.
- The stack + instruction pointer represents the execution state of each thread.
- Stack frames accumulate as functions are called.

[Stack Properties]
- Allocated when a thread is created.
- Stack size is fixed depending on the platform.
- Stack overflow exceptions can occur with too many function calls.

[Heap]
- Objects, class members, and static variables can be allocated here.
- Managed by Garbage Collection (GC). Objects exist as long as they are referenced.
- Class members exist as long as their parent objects do.
- Static variables exist indefinitely.

[Object vs References]
- References can be allocated on the stack and, if part of a class member, on the heap.
- Objects can only be allocated on the heap.

[Stack vs Heap]
![stack_vs_heap](img/stack_vs_heap.png)

[Resources?]
- Variables
- Data structures
- File or connection handles
- Message or work queues
-> All elements allocated on the heap.

[Why do we need Resource Sharing?]
- When a work queue exists, and multiple threads take tasks from it.
- In environments where multiple HTTP request threads manipulate a database.

[Challenges of Multithreaded Programming]
- If multiple threads perform non-atomic operations on shared resources, unexpected results can occur.

</details>

<details>
<summary>6. Concurrency Challenge</summary>

[Critical Section]
- Code that multiple threads should not access simultaneously.

[Synchronized - Monitor/Lock]
- Java's `synchronized` keyword can be used to restrict access to a method or block to one thread at a time.
- Using it on a method restricts all synchronized methods in the class to one thread at a time.
- Using it on a block requires passing a locking object as an argument.
- This technique is called a monitor.
- Synchronized blocks are reentrant.

[Atomic Operation?]
- All reference assignments.
- All assignments to primitive types except long and double.
    - Long and double are 64-bit types and may not guarantee atomicity -> Use the volatile keyword to ensure atomicity.

[Race Condition]
- Occurs when two or more threads access a shared resource, with at least one thread attempting to modify it.
- Depending on thread scheduling, this can lead to incorrect results.
-> The solution is to identify and protect the critical section.

[Data Race]
- Compilers and CPUs may reorder instructions for performance optimization, which can violate logical consistency.
- The following code executes sequentially:
```java
public void someFunction() {
    s = 1;
    x = s + 2;
    z = x + 4;
}
```
- However, the following can be reordered:
```java
public void increment() {
    x++;
    y++;
}

public void decrement() {
    x--;
    y--;
}
```
- To avoid this, we can:
    - Use the synchronized keyword -> but this is not ideal for situations without a race condition as it can waste multithreading benefits.
    -

 Use `java.util.concurrent.locks.Lock`.

</details>

<details>
<summary>7. Advanced Thread Coordination</summary>

[Inter-Thread Communication]
- Threads can communicate through shared data.
- Using `wait()`, `notify()`, and `notifyAll()` helps in synchronizing threads.
- `wait()` releases the lock and puts the current thread in a waiting state until notified.
- `notify()` wakes a single waiting thread, while `notifyAll()` wakes all waiting threads.

[Locks]
- Explicit locks can be used for more granular control over synchronization.
- Locks can be implemented through `java.util.concurrent.locks`.

</details>

<details>
<summary>8. Conclusion</summary>

- Threads can be complicated to manage due to performance overheads and critical sections.
- It’s essential to understand the cost of parallelization and what can or cannot be divided into subtasks.
- Designing a robust multithreaded application requires clear knowledge of synchronization and shared resource management.
- Always consider performance trade-offs when optimizing multithreaded code.

</details>
